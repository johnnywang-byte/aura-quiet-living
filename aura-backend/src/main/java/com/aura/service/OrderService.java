package com.aura.service;

import com.aura.model.dto.OrderRequest;
import com.aura.model.entity.Order;
import com.aura.model.entity.OrderItem;
import com.aura.model.entity.Product;
import com.aura.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 订单业务服务层（完全适配你的Order + OrderItem实体）
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;

    // 订单状态常量（匹配你的Order实体默认值）
    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_SHIPPED = "SHIPPED";
    private static final String STATUS_DELIVERED = "DELIVERED";
    private static final String STATUS_CANCELLED = "CANCELLED";

    /**
     * 创建新订单（适配你的OrderItem实体：price字段、无itemAmount）
     */
    @Transactional(rollbackFor = Exception.class)
    public Order createOrder(OrderRequest request) {
        // 1. 参数校验（逻辑不变，仅适配提示）
        if (request == null) {
            log.warn("Order creation request is null");
            throw new IllegalArgumentException("Order creation request cannot be null");
        }
        if (!StringUtils.hasText(request.getCustomerName())) {
            throw new IllegalArgumentException("Customer name cannot be empty");
        }
        if (!StringUtils.hasText(request.getShippingAddress())) {
            throw new IllegalArgumentException("Shipping address cannot be empty");
        }
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("At least one product must be selected");
        }
        for (OrderRequest.OrderItemRequest itemReq : request.getItems()) {
            if (itemReq == null || !StringUtils.hasText(itemReq.getProductId()) ||
                    itemReq.getQuantity() == null || itemReq.getQuantity() <= 0) {
                throw new IllegalArgumentException(
                        "Invalid order item: Product ID cannot be empty and quantity must be greater than 0");
            }
        }

        try {
            // 2. 库存校验 + 预查询产品信息
            Map<String, Product> productMap = new HashMap<>();
            for (OrderRequest.OrderItemRequest itemReq : request.getItems()) {
                String productId = itemReq.getProductId();
                int quantity = itemReq.getQuantity();

                // 查询产品（不存在抛异常）
                Product product = productService.getProductById(productId);
                productMap.put(productId, product);

                // 库存校验
                if (!productService.checkInventory(productId, quantity)) {
                    log.warn("Insufficient stock for product {} ({}): current={}, required={}", productId,
                            product.getName(), product.getStock(), quantity);
                    throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
                }
            }

            // 3. 构建订单主表对象
            Order order = new Order();
            String orderNumber = generateOrderNumber();
            order.setOrderNumber(orderNumber);
            // 填充客户信息
            order.setCustomerName(request.getCustomerName());
            order.setCustomerEmail(request.getCustomerEmail());
            order.setCustomerPhone(request.getCustomerPhone());
            order.setShippingAddress(request.getShippingAddress());
            // 实体默认值：status=PENDING, paymentMethod=MOCK, paymentStatus=PAID，无需手动设置

            // 4. 构建订单项（适配你的OrderItem字段）
            BigDecimal totalAmount = BigDecimal.ZERO;
            List<OrderItem> orderItems = new ArrayList<>();
            for (OrderRequest.OrderItemRequest itemReq : request.getItems()) {
                String productId = itemReq.getProductId();
                int quantity = itemReq.getQuantity();
                Product product = productMap.get(productId);

                // 创建OrderItem（匹配你的实体字段）
                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order); // 关联订单
                orderItem.setProductId(productId);
                orderItem.setProductName(product.getName()); // 填充产品名称（非必填，但建议赋值）
                orderItem.setQuantity(quantity);
                orderItem.setPrice(product.getPrice()); // 单价字段为price，而非unitPrice

                // 计算单品金额并累加总金额
                BigDecimal itemAmount = product.getPrice().multiply(BigDecimal.valueOf(quantity))
                        .setScale(2, RoundingMode.HALF_UP);
                orderItem.setSubtotal(itemAmount); // 设置 subtotal 字段
                totalAmount = totalAmount.add(itemAmount);

                orderItems.add(orderItem);
            }

            // 设置订单总金额（保留2位小数）
            order.setTotalAmount(totalAmount.setScale(2, RoundingMode.HALF_UP));
            // 关联订单项到订单
            order.setItems(orderItems);

            // 5. 保存订单（级联保存OrderItem）
            Order savedOrder = orderRepository.save(order);
            log.info("Order created successfully: number={}, customer={}, total={}, items={}",
                    orderNumber, request.getCustomerName(), totalAmount, orderItems.size());

            // 6. 扣减产品库存
            for (OrderRequest.OrderItemRequest itemReq : request.getItems()) {
                String productId = itemReq.getProductId();
                int quantity = itemReq.getQuantity();
                productService.updateStock(productId, -quantity);
                log.info("Stock deducted for product {}: quantity={}, order={}", productId, quantity, orderNumber);
            }

            return savedOrder;
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            throw e; // 业务异常直接抛出
        } catch (Exception e) {
            log.error("Failed to create order", e);
            throw new RuntimeException("Failed to create order, please try again later");
        }
    }

    /**
     * 按订单号查询订单（包含订单项，避免懒加载）
     */
    public Order getOrderByNumber(String orderNumber) {
        if (!StringUtils.hasText(orderNumber)) {
            throw new IllegalArgumentException("Order number cannot be empty");
        }

        try {
            Order order = orderRepository.findByOrderNumber(orderNumber)
                    .orElseThrow(() -> new EntityNotFoundException("Order not found: " + orderNumber));
            log.info("Order found: number={}, customer={}, status={}, total={}, items={}",
                    orderNumber, order.getCustomerName(), order.getStatus(),
                    order.getTotalAmount(), order.getItems().size());
            return order;
        } catch (EntityNotFoundException | IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to get order by number: {}", orderNumber, e);
            throw new RuntimeException("Failed to query order, please try again later");
        }
    }

    /**
     * 查询所有订单
     */
    public List<Order> getAllOrders() {
        try {
            List<Order> orders = orderRepository.findAll();
            log.info("Found {} orders", orders.size());
            return orders;
        } catch (Exception e) {
            log.error("Failed to get all orders", e);
            return Collections.emptyList();
        }
    }

    /**
     * 更新订单收货地址（仅待处理/未发货订单可更新）
     */
    @Transactional(rollbackFor = Exception.class)
    public Order updateShippingAddress(String orderNumber, String newAddress) {
        if (!StringUtils.hasText(orderNumber) || !StringUtils.hasText(newAddress)) {
            throw new IllegalArgumentException("Order number and new address cannot be empty");
        }

        Order order = getOrderByNumber(orderNumber);
        // 校验状态：只有待处理和处理中状态允许更新地址（已发货/已送达/已取消不允许）
        /*
         * Order Status Categories
         * Modification Prohibited:
         * ✅ SHIPPED
         * ✅ DELIVERED
         * ✅ CANCELLED
         * Modification Allowed:
         * ✅ PENDING
         * ✅ PROCESSING (If this status exists)
         */
        String status = order.getStatus();
        List<String> allowedStatuses = Arrays.asList(STATUS_PENDING, "PROCESSING");
        if (!allowedStatuses.contains(status)) {
            throw new IllegalArgumentException(
                    String.format("Cannot update shipping address for order with status: %s. " +
                            "Only PENDING or PROCESSING orders can be modified.", status));
        }

        order.setShippingAddress(newAddress);
        Order updatedOrder = orderRepository.save(order);
        log.info("Shipping address updated for order {}: {} → {}", orderNumber, order.getShippingAddress(), newAddress);
        return updatedOrder;
    }

    /**
     * 更新订单状态
     */
    @Transactional(rollbackFor = Exception.class)
    public Order updateOrderStatus(String orderNumber, String status) {
        if (!StringUtils.hasText(orderNumber) || !StringUtils.hasText(status)) {
            throw new IllegalArgumentException("Order number and status cannot be empty");
        }

        // 校验状态合法性
        List<String> validStatus = Arrays.asList(STATUS_PENDING, STATUS_SHIPPED, STATUS_DELIVERED, STATUS_CANCELLED);
        if (!validStatus.contains(status)) {
            throw new IllegalArgumentException("Order status must be one of: " + String.join(", ", validStatus));
        }

        Order order = getOrderByNumber(orderNumber);
        String oldStatus = order.getStatus();

        // 状态转换规则校验
        validateStatusTransition(oldStatus, status);

        // 更新状态
        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        log.info("Order status updated for {}: {} → {}", orderNumber, oldStatus, status);

        // 取消订单时恢复库存
        if (STATUS_CANCELLED.equals(status) && !STATUS_CANCELLED.equals(oldStatus)) {
            restoreStock(order);
            log.info("Order {} cancelled, stock restored", orderNumber);
        }

        return updatedOrder;
    }

    /**
     * 校验订单状态转换是否合法
     * 
     * @param oldStatus 当前状态
     * @param newStatus 目标状态
     * @throws IllegalArgumentException 如果状态转换不合法
     */
    private void validateStatusTransition(String oldStatus, String newStatus) {
        // 如果状态没变化，允许
        if (oldStatus.equals(newStatus)) {
            return;
        }

        // 定义合法的状态转换规则
        switch (oldStatus) {
            case STATUS_PENDING:
                // PENDING 可以转换为：SHIPPED（发货）或 CANCELLED（取消）
                if (!STATUS_SHIPPED.equals(newStatus) && !STATUS_CANCELLED.equals(newStatus)) {
                    throw new IllegalArgumentException(
                            String.format("Cannot change order status from PENDING to %s. " +
                                    "PENDING orders can only be changed to SHIPPED or CANCELLED.", newStatus));
                }
                break;

            case STATUS_SHIPPED:
                // SHIPPED 只能转换为：DELIVERED（送达）
                if (STATUS_CANCELLED.equals(newStatus)) {
                    throw new IllegalArgumentException(
                            "ORDER_ALREADY_SHIPPED: The order has already been shipped and cannot be cancelled directly. "
                                    +
                                    "Please contact our customer service team for assistance.");
                } else if (!STATUS_DELIVERED.equals(newStatus)) {
                    throw new IllegalArgumentException(
                            String.format("Cannot change order status from SHIPPED to %s. " +
                                    "SHIPPED orders can only be changed to DELIVERED.", newStatus));
                }
                break;

            case STATUS_DELIVERED:
                // DELIVERED 是终态，不允许任何转换
                if (STATUS_CANCELLED.equals(newStatus)) {
                    throw new IllegalArgumentException(
                            "ORDER_ALREADY_DELIVERED: The order has already been delivered and cannot be cancelled. " +
                                    "If you need to return the product, please contact our customer service team.");
                } else {
                    throw new IllegalArgumentException(
                            "Cannot change status of DELIVERED orders. The order is complete.");
                }

            case STATUS_CANCELLED:
                // CANCELLED 是终态，不允许任何转换
                throw new IllegalArgumentException(
                        "ORDER_ALREADY_CANCELLED: This order has already been cancelled and cannot be modified. " +
                                "If you need to place a new order, please create a new one.");

            default:
                throw new IllegalArgumentException("Unknown order status: " + oldStatus);
        }

        log.info("Status transition validated: {} → {}", oldStatus, newStatus);
    }

    /*
     * //更新物流单号（仅已发货订单可更新）尚未实现此功能
     * 
     * @Transactional(rollbackFor = Exception.class)
     * public Order updateTrackingNumber(String orderNumber, String trackingNumber)
     * {
     * if (!StringUtils.hasText(orderNumber) ||
     * !StringUtils.hasText(trackingNumber)) {
     * throw new
     * IllegalArgumentException("Order number and tracking number cannot be empty");
     * }
     * 
     * Order order = getOrderByNumber(orderNumber);
     * if (!STATUS_SHIPPED.equals(order.getStatus())) {
     * throw new
     * IllegalArgumentException("Only shipped orders can update tracking number");
     * }
     * 
     * order.setTrackingNumber(trackingNumber);
     * Order updatedOrder = orderRepository.save(order);
     * log.info("Tracking number updated for order {}: {}", orderNumber,
     * trackingNumber);
     * return updatedOrder;
     * }
     */

    /**
     * 生成唯一订单号（格式：ORD-yyyyMMddHHmmss-随机4位数字）
     */
    private String generateOrderNumber() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String randomPart = String.format("%04d", (int) (Math.random() * 10000));
        return "ORD-" + datePart + "-" + randomPart;
    }

    /**
     * 恢复订单的产品库存（适配你的OrderItem字段）
     */
    private void restoreStock(Order order) {
        if (order.getItems() == null || order.getItems().isEmpty()) {
            log.warn("Order {} has no items, no stock to restore", order.getOrderNumber());
            return;
        }

        try {
            for (OrderItem orderItem : order.getItems()) {
                String productId = orderItem.getProductId();
                int quantity = orderItem.getQuantity();
                productService.updateStock(productId, quantity); // 正数=增加库存
                log.info("Stock restored for product {}: quantity={}, order={}", productId, quantity,
                        order.getOrderNumber());
            }
        } catch (Exception e) {
            log.error("Failed to restore stock for order {}", order.getOrderNumber(), e);
            throw new RuntimeException("Failed to restore stock");
        }
    }
}