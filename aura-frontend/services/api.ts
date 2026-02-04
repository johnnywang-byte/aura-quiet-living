/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import { API_BASE_URL } from '../constants';
import { Product } from '../types';

// ===== API Response Types =====

export interface ApiResponse<T> {
    success: boolean;
    data: T | null;
    message: string | null;
}

export interface ChatRequest {
    message: string;
    sessionId?: string;
    context?: {
        currentPage?: string;
        viewingProductId?: string;
        lastOrderNumber?: string;
    };
}

export interface ChatResponse {
    message: string;
    sessionId: string;
    suggestedProducts?: string[];
    suggestedActions?: Array<{
        type: string;
        productId?: string;
        label: string;
    }>;
    timestamp: string;
}

export interface OrderRequest {
    customerName: string;
    customerEmail: string;
    customerPhone: string;
    shippingAddress: string;
    items: Array<{
        productId: string;
        quantity: number;
    }>;
}

export interface Order {
    id: number;
    orderNumber: string;
    customerName: string;
    customerEmail: string;
    customerPhone: string;
    shippingAddress: string;
    totalAmount: number;
    status: string;
    paymentStatus: string;
    createdAt: string;
    updatedAt: string;
    items: Array<{
        id: number;
        productId: string;
        productName: string;
        quantity: number;
        unitPrice: number;
        subtotal: number;
    }>;
}

// ===== Helper Functions =====

async function fetchApi<T>(
    endpoint: string,
    options?: RequestInit
): Promise<ApiResponse<T>> {
    try {
        const response = await fetch(`${API_BASE_URL}${endpoint}`, {
            ...options,
            headers: {
                'Content-Type': 'application/json',
                ...options?.headers,
            },
        });

        const data: ApiResponse<T> = await response.json();
        return data;
    } catch (error) {
        console.error('API Error:', error);
        return {
            success: false,
            data: null,
            message: error instanceof Error ? error.message : 'Unknown error',
        };
    }
}

// ===== Product API =====

export const productApi = {
    /**
     * Get all products
     */
    async getAllProducts(): Promise<Product[]> {
        const response = await fetchApi<Product[]>('/products');
        return response.data || [];
    },

    /**
     * Get product by ID
     */
    async getProductById(id: string): Promise<Product | null> {
        const response = await fetchApi<Product>(`/products/${id}`);
        return response.data;
    },

    /**
     * Get products by category
     */
    async getProductsByCategory(category: string): Promise<Product[]> {
        const response = await fetchApi<Product[]>(`/products/category/${category}`);
        return response.data || [];
    },

    /**
     * Search products
     */
    async searchProducts(keyword: string): Promise<Product[]> {
        const response = await fetchApi<Product[]>(`/products/search?q=${encodeURIComponent(keyword)}`);
        return response.data || [];
    },
};

// ===== Order API =====

export const orderApi = {
    /**
     * Create new order
     */
    async createOrder(orderRequest: OrderRequest): Promise<ApiResponse<Order>> {
        return await fetchApi<Order>('/orders', {
            method: 'POST',
            body: JSON.stringify(orderRequest),
        });
    },

    /**
     * Get order by order number
     */
    async getOrder(orderNumber: string): Promise<Order | null> {
        const response = await fetchApi<Order>(`/orders/${orderNumber}`);
        return response.data;
    },

    /**
     * Get all orders
     */
    async getAllOrders(): Promise<Order[]> {
        const response = await fetchApi<Order[]>('/orders');
        return response.data || [];
    },

    /**
     * Update shipping address
     */
    async updateAddress(orderNumber: string, newAddress: string): Promise<ApiResponse<Order>> {
        return await fetchApi<Order>(`/orders/${orderNumber}/address`, {
            method: 'PUT',
            body: JSON.stringify(newAddress),
        });
    },
};

// ===== AI Chat API =====

export const chatApi = {
    /**
     * Send chat message to AI
     */
    async sendMessage(request: ChatRequest): Promise<ChatResponse | null> {
        const response = await fetchApi<ChatResponse>('/ai/chat', {
            method: 'POST',
            body: JSON.stringify(request),
        });
        return response.data;
    },
};

// ===== Export all APIs =====

export default {
    product: productApi,
    order: orderApi,
    chat: chatApi,
};
