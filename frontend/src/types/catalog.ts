export interface Plan {
  id: number
  code: string
  durationDays: number
}

export interface Benefit {
  code: string
  description: string
  configValue: string | null
}

export interface Tier {
  id: number
  code: string
  rank: number
  benefits: Benefit[]
}

export interface Price {
  planId: number
  planCode: string
  tierId: number
  tierCode: string
  price: number
  currency: string
}
