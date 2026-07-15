import { apiClient } from './client'
import type { Plan, Tier, Price } from '../types/catalog'

export const catalogApi = {
  getPlans: () => apiClient.get<Plan[]>('/plans'),
  getTiers: () => apiClient.get<Tier[]>('/tiers'),
  getPricing: () => apiClient.get<Price[]>('/pricing'),
}
