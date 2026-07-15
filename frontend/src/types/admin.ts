export interface BenefitDefinition {
  id: number
  code: string
  description: string | null
}

export interface TierBenefit {
  id: number
  tierId: number
  tierCode: string
  benefitId: number
  benefitCode: string
  configValue: string | null
  active: boolean
}

export interface TierCriteria {
  id: number
  tierId: number
  tierCode: string
  criteriaType: string
  configValue: string | null
  active: boolean
}
