import { useEffect, useState } from 'react'
import { catalogApi } from '../api/catalog'
import { subscriptionApi } from '../api/subscription'
import { ApiError } from '../api/client'
import type { Plan, Tier, Price } from '../types/catalog'
import { PlanTierPicker } from '../components/PlanTierPicker'
import { TierBenefitsList } from '../components/TierBenefitsList'

export function PlansPage({ userId, onSubscribed }: { userId: number; onSubscribed: () => void }) {
  const [plans, setPlans] = useState<Plan[]>([])
  const [tiers, setTiers] = useState<Tier[]>([])
  const [pricing, setPricing] = useState<Price[]>([])
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    Promise.all([catalogApi.getPlans(), catalogApi.getTiers(), catalogApi.getPricing()])
      .then(([plansRes, tiersRes, pricingRes]) => {
        setPlans(plansRes)
        setTiers(tiersRes)
        setPricing(pricingRes)
      })
      .catch((err) => setError(err instanceof Error ? err.message : String(err)))
      .finally(() => setLoading(false))
  }, [])

  async function handleSubscribe(planId: number, tierId: number) {
    setError(null)
    try {
      await subscriptionApi.subscribe(userId, planId, tierId)
      onSubscribed()
    } catch (err) {
      setError(err instanceof ApiError ? err.message : String(err))
    }
  }

  if (loading) return <p>Loading plans...</p>

  return (
    <section>
      <h2>Plans & Tiers</h2>
      {error && <p style={{ color: '#cf222e' }}>{error}</p>}
      <PlanTierPicker plans={plans} tiers={tiers} pricing={pricing} actionLabel="Subscribe" onSelect={handleSubscribe} />

      <h3>Tier benefits</h3>
      {tiers.map((tier) => (
        <div key={tier.id} style={{ marginBottom: 12 }}>
          <strong>{tier.code}</strong>
          <TierBenefitsList benefits={tier.benefits} />
        </div>
      ))}
    </section>
  )
}
