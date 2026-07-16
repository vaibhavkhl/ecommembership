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
  pricePaid: number | null
  currency: string | null
}

export type SubscriptionEventType = 'SUBSCRIBED' | 'UPGRADED' | 'DOWNGRADED' | 'PLAN_CHANGED' | 'CANCELLED'

export interface SubscriptionEvent {
  id: number
  eventType: SubscriptionEventType
  fromPlanCode: string | null
  toPlanCode: string | null
  fromTierCode: string | null
  toTierCode: string | null
  pricePaid: number | null
  currency: string | null
  createdAt: string
}
