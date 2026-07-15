import { apiClient } from './client'
import type { BenefitDefinition, TierBenefit, TierCriteria } from '../types/admin'

export const adminApi = {
  listBenefitDefinitions: () => apiClient.get<BenefitDefinition[]>('/admin/benefit-definitions'),
  createBenefitDefinition: (code: string, description: string) =>
    apiClient.post<BenefitDefinition>('/admin/benefit-definitions', { code, description }),

  listTierBenefits: () => apiClient.get<TierBenefit[]>('/admin/tier-benefits'),
  createTierBenefit: (tierId: number, benefitId: number, configValue: string) =>
    apiClient.post<TierBenefit>('/admin/tier-benefits', { tierId, benefitId, configValue: configValue || null }),
  updateTierBenefit: (id: number, configValue: string, active: boolean) =>
    apiClient.put<TierBenefit>(`/admin/tier-benefits/${id}`, { configValue: configValue || null, active }),

  listTierCriteria: () => apiClient.get<TierCriteria[]>('/admin/tier-criteria'),
  createTierCriteria: (tierId: number, criteriaType: string, configValue: string) =>
    apiClient.post<TierCriteria>('/admin/tier-criteria', { tierId, criteriaType, configValue: configValue || null }),
  updateTierCriteria: (id: number, configValue: string, active: boolean) =>
    apiClient.put<TierCriteria>(`/admin/tier-criteria/${id}`, { configValue: configValue || null, active }),
}
