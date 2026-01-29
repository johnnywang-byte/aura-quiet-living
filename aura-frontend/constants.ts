/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

// API Configuration
export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

// Brand Constants
export const BRAND_NAME = 'Aura';
export const BRAND_TAGLINE = 'Quiet Living';
export const BRAND_DESCRIPTION = 'Technology that feels like nature. Designed for those who seek calm in a connected world.';

// Product Categories
export const CATEGORIES = ['Audio', 'Wearable', 'Mobile', 'Home'] as const;
export type Category = typeof CATEGORIES[number];

// Note: Product and Journal data are now fetched from the backend API
// See services/api.ts for API integration