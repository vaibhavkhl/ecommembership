export type SubscriptionStatus = 'ACTIVE' | 'CANCELLED' | 'EXPIRED' | 'PENDING'

export interface PlanSummary {
  id: number
  code: string
}

export interface TierSummary {
  id: number
  code: string
  rank: number
}

export interface Subscription {
  id: number
  userId: number
  plan: PlanSummary
  tier: TierSummary
  status: SubscriptionStatus
  startDate: string
  endDate: string
  autoRenew: boolean
  expired: boolean
}
