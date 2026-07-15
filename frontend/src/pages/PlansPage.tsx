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

  if (loading) return <p className="text-slate-500 dark:text-slate-400">Loading plans...</p>

  return (
    <section className="space-y-8">
      <div>
        <h2 className="mb-3 text-xl font-semibold text-slate-900 dark:text-slate-100">Plans &amp; Tiers</h2>
        {error && <p className="mb-3 text-sm text-red-600 dark:text-red-400">{error}</p>}
        <PlanTierPicker plans={plans} tiers={tiers} pricing={pricing} actionLabel="Subscribe" onSelect={handleSubscribe} />
      </div>

      <div>
        <h3 className="mb-3 text-lg font-semibold text-slate-900 dark:text-slate-100">Tier benefits</h3>
        <div className="grid gap-4 sm:grid-cols-3">
          {tiers.map((tier) => (
            <div key={tier.id} className="rounded-lg border border-slate-200 bg-white p-4 dark:border-slate-700 dark:bg-slate-800">
              <p className="mb-2 font-semibold text-slate-900 dark:text-slate-100">{tier.code}</p>
              <TierBenefitsList benefits={tier.benefits} />
            </div>
          ))}
        </div>
      </div>
    </section>
  )
}
