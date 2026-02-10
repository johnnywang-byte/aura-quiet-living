package com.aura.service;

import com.aura.model.entity.Product;
import com.aura.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

/**
 * 产品核心业务服务
 * 
 * 职责：
 * 1. 产品基础查询（全部产品、按ID、按分类）
 * 2. 产品搜索（关键词搜索、批量查询）
 * 3. 库存管理（检查库存、更新库存）
 * 4. 产品筛选（按条件过滤）
 * 
 * 设计原则：
 * - 所有查询都通过此 Service 层，Function 层不直接访问 Repository
 * - 使用 Stream API 实现灵活的搜索和过滤
 * - 统一异常处理，返回空集合而不是抛异常（查询类方法）
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    // ==================== 基础查询 ====================
    
    /**
     * 获取所有产品
     * 
     * 用途：
     * - 前端首页展示所有产品
     * - AI Agent 获取产品列表
     * 
     * @return 产品列表，如果出错返回空列表
     */
    public List<Product> getAllProducts() {
        try {
            log.info("Getting all products");
            List<Product> products = productRepository.findAll();
            log.info("Found {} products", products.size());
            return products;
        } catch (Exception e) {
            log.error("Failed to get all products", e);
            return Collections.emptyList();  // 返回空列表，不抛异常
        }
    }

    /**
     * 根据产品ID查询单个产品
     * 
     * 调用方：
     * - OrderService（创建订单时验证产品）
     * - UpdateStock（更新库存时查询产品）
     * 
     * @param id 产品ID（例如："harmony", "pulse"）
     * @return 产品对象
     * @throws IllegalArgumentException 如果ID为空
     * @throws EntityNotFoundException 如果产品不存在
     */
    public Product getProductById(String id) {
        if (!StringUtils.hasText(id)) {
            throw new IllegalArgumentException("Product ID cannot be empty");
        }
        log.debug("Getting product by ID: {}", id);
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + id));
    }

    /**
     * 根据分类查询产品
     * 
     * 实现：
     * - 使用 Stream API 过滤，不依赖 Repository 自定义方法
     * - 忽略大小写匹配（equalsIgnoreCase）
     * 
     * @param category 产品分类（例如："headphones", "watches"）
     * @return 该分类下的所有产品，如果分类为空或出错返回空列表
     */
    public List<Product> getProductsByCategory(String category) {
        if (!StringUtils.hasText(category)) {
            return Collections.emptyList();
        }
        try {
            log.info("Getting products by category: {}", category);
            // ✅ 使用stream过滤，不依赖Repository方法
            return productRepository.findAll().stream()
                    .filter(p -> category.equalsIgnoreCase(p.getCategory()))
                    .toList();
        } catch (Exception e) {
            log.error("Failed to get products by category: {}", category, e);
            return Collections.emptyList();
        }
    }

    // ==================== 搜索/批量查询 ====================
    
    /**
     * 关键词搜索产品（核心搜索方法）
     * 
     * 调用方：
     * - ProductExpertAgent（用户询问产品时）
     * - SearchProductsFunction（AI Function Calling）
     * 
     * 搜索算法：
     * 1. 分词：将关键词按空格拆分（例如："Aura Harmony" → ["aura", "harmony"]）
     * 2. 搜索范围：产品名称 + tagline + 描述
     * 3. 匹配规则：任意一个词匹配即返回（OR 逻辑）
     * 4. 过滤：忽略长度 ≤ 2 的词（例如："me", "it"）
     * 
     * 示例：
     * - 输入："Tell me about Aura Harmony" 
     * - 分词：["tell", "me", "about", "aura", "harmony"]
     * - 有效词：["tell", "about", "aura", "harmony"]（过滤掉"me"）
     * - 匹配：找到包含 "aura" 或 "harmony" 的产品
     * 
     * @param keyword 搜索关键词（支持多词，空格分隔）
     * @return 匹配的产品列表，如果关键词为空或出错返回空列表
     */
    public List<Product> searchProducts(String keyword) { //只搜索 SQL 数据库！
        if (!StringUtils.hasText(keyword)) {
            return Collections.emptyList();
        }
        try {
            log.info("Searching products with keyword: {}", keyword);
            
            // 步骤1: 转为小写，统一处理
            String lowerKeyword = keyword.toLowerCase();

            // 步骤2: 分词 - 按空格拆分关键词
            // 例如："Tell me about Aura Harmony" → ["tell", "me", "about", "aura", "harmony"]
            String[] keywords = lowerKeyword.split("\\s+");

            // 步骤3: 使用 Stream API 过滤产品
            // 
            // Stream API 工作原理：
            // 1. productRepository.findAll() → 获取所有产品（例如：6个产品）
            // 2. .stream() → 转为流，可以进行链式操作
            // 3. .filter(条件) → 保留满足条件的产品，过滤掉不满足的
            // 4. .toList() → 将过滤后的结果转回列表
            //
            // 例如：有 6 个产品，只有 2 个匹配关键词 → 返回 2 个产品
            List<Product> results = productRepository.findAll().stream()
                    .filter(p -> {
                        // filter() 会对每个产品（p）执行这个检查
                        // 如果返回 true → 保留这个产品
                        // 如果返回 false → 过滤掉这个产品
                        
                        // === 对于当前这个产品 p，检查是否匹配关键词 ===
                        
                        // 子步骤1: 构建搜索文本（把产品的所有文本信息合并）
                        // 
                        // 假设产品 p 是 Harmony 耳机：
                        // - p.getName() = "Harmony"
                        // - p.getTagline() = "Perfect sound quality"
                        // - p.getDescription() = "Wireless noise-cancelling headphones..."
                        // 
                        // 合并后：searchText = "harmony perfect sound quality wireless noise-cancelling headphones..."
                        String searchText = (p.getName() + " " +
                                (p.getTagline() != null ? p.getTagline() : "") + " " +
                                (p.getDescription() != null ? p.getDescription() : "")).toLowerCase();

                        // 子步骤2: 检查关键词是否在 searchText 中
                        // 
                        // 回顾：keywords = ["tell", "me", "about", "aura", "harmony"]
                        // 
                        // 对每个关键词进行检查：
                        for (String kw : keywords) {
                            // 过滤短词：忽略长度 ≤ 2 的词（"me", "it" 等）
                            // 检查匹配：searchText 是否包含这个关键词
                            //
                            // 示例检查过程：
                            // - "tell" (长度4) → searchText.contains("tell") → false（不匹配）
                            // - "me" (长度2) → 跳过（长度 ≤ 2）
                            // - "about" (长度5) → searchText.contains("about") → false（不匹配）
                            // - "aura" (长度4) → searchText.contains("aura") → false（不匹配）
                            // - "harmony" (长度7) → searchText.contains("harmony") → true（匹配！）
                            if (kw.length() > 2 && searchText.contains(kw)) {
                                return true;  // 找到匹配！保留这个产品
                                // 只要找到一个关键词匹配，立即返回 true，不再检查其他关键词
                            }
                        }
                        
                        // 所有关键词都检查完了，没有一个匹配
                        return false;  // 过滤掉这个产品
                    })
                    .toList();  // 把保留下来的产品转回列表

            log.info("Found {} products matching keyword: {}", results.size(), keyword);
            return results;
        } catch (Exception e) {
            log.error("Failed to search products with keyword: {}", keyword, e);
            return Collections.emptyList();
        }
    }

    /**
     * 批量查询产品（根据多个ID）
     * 
     * 调用方：
     * - OrderService（创建订单时批量验证产品）
     * - 购物车功能（批量获取产品详情）
     * 
     * 实现：使用 Stream API 过滤，不依赖 Repository 自定义方法
     * 
     * @param ids 产品ID列表（例如：["harmony", "pulse", "flow"]）
     * @return 对应的产品列表，如果IDs为空或出错返回空列表
     */
    public List<Product> findByIdIn(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            log.info("Finding products by IDs: {}", ids);
            // ✅ 使用stream实现，不依赖Repository方法
            return productRepository.findAll().stream()
                    .filter(p -> ids.contains(p.getId()))
                    .toList();
        } catch (Exception e) {
            log.error("Failed to find products by IDs: {}", ids, e);
            return Collections.emptyList();
        }
    }

    // ==================== 库存管理 ====================
    
    /**
     * 检查库存是否充足
     * 
     * 调用方：
     * - CheckInventoryFunction（AI 查询库存时）
     * - OrderService（创建订单前验证库存）
     * 
     * 校验规则：
     * - 产品必须存在
     * - 产品库存 ≥ 请求数量
     * 
     * @param productId 产品ID
     * @param quantity 请求数量
     * @return true 库存充足，false 库存不足或产品不存在
     */
    public boolean checkInventory(String productId, int quantity) {
        if (!StringUtils.hasText(productId) || quantity <= 0) {
            return false;
        }
        try {
            Product product = getProductById(productId);
            boolean available = product.getStock() != null && product.getStock() >= quantity;
            log.debug("Inventory check for {}: requested={}, available={}, result={}",
                    productId, quantity, product.getStock(), available);
            return available;
        } catch (EntityNotFoundException e) {
            log.warn("Product not found for inventory check: {}", productId);
            return false;
        } catch (Exception e) {
            log.error("Inventory check failed for product: {}, quantity: {}", productId, quantity, e);
            return false;
        }
    }

    /**
     * 更新产品库存（扣减或增加）
     * 
     * 调用方：
     * - OrderService.createOrder（创建订单时扣减库存）
     * - OrderService.updateOrderStatus（取消订单时恢复库存）
     * 
     * 使用场景：
     * 1. 下单：change = -2（扣减2个库存）
     * 2. 取消订单：change = +2（恢复2个库存）
     * 
     * 安全校验：
     * - 使用 @Transactional 保证原子性
     * - 不允许库存变为负数（扣减时检查）
     * 
     * @param productId 产品ID
     * @param change 库存变化量（负数=扣减，正数=增加）
     * @throws IllegalArgumentException 如果库存不足或产品ID为空
     * @throws EntityNotFoundException 如果产品不存在
     */
    @Transactional // ✅ 添加事务注解，保证库存更新的原子性
    public void updateStock(String productId, int change) {
        if (!StringUtils.hasText(productId)) {
            throw new IllegalArgumentException("Product ID cannot be empty");
        }

        // 步骤1: 查询产品
        Product product = getProductById(productId);
        Integer currentStock = product.getStock() != null ? product.getStock() : 0;
        
        // 步骤2: 计算新库存
        int newStock = currentStock + change;

        // 步骤3: 校验库存不能为负
        if (newStock < 0) {
            throw new IllegalArgumentException(
                    String.format("Insufficient stock. Current: %d, Attempted to deduct: %d", 
                        currentStock, -change));
        }

        // 步骤4: 更新并保存
        product.setStock(newStock);
        productRepository.save(product);

        log.info("Stock updated for product {}: {} → {} (change: {})",
                productId, currentStock, newStock, change > 0 ? "+" + change : change);
    }

    // ==================== 筛选（匹配推荐功能） ====================
    
    /**
     * 根据条件筛选产品（返回产品ID列表）
     * 
     * 用途：
     * - 未来可扩展为智能推荐功能
     * - 根据用户偏好筛选产品
     * 
     * 当前实现：
     * - 简化版本，返回所有产品ID
     * - 可扩展为解析 criteria（例如："price<300", "category=headphones"）
     * 
     * @param criteria 筛选条件（预留字段，当前未解析）
     * @return 产品ID列表，如果条件为空或出错返回空列表
     */
    public List<String> filterProductsByCriteria(String criteria) {
        if (!StringUtils.hasText(criteria)) {
            return Collections.emptyList();
        }
        try {
            log.info("Filtering products by criteria: {}", criteria);
            // ✅ 简化实现，实际可以根据criteria解析条件
            List<Product> products = getAllProducts();
            return products.stream().map(Product::getId).toList();
        } catch (Exception e) {
            log.error("Failed to filter products by criteria: {}", criteria, e);
            return Collections.emptyList();
        }
    }

    /**
     * 根据ID列表获取产品详情（别名方法）
     * 
     * 作用：语义化的方法名，实际调用 findByIdIn()
     * 
     * @param ids 产品ID列表
     * @return 产品详情列表
     */
    public List<Product> getProductDetailsByIds(List<String> ids) {
        return findByIdIn(ids);
    }
}