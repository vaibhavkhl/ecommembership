import { useCallback, useEffect, useState } from 'react'
import { catalogApi } from '../api/catalog'
import { subscriptionApi } from '../api/subscription'
import { ApiError } from '../api/client'
import type { Plan, Tier, Price } from '../types/catalog'
import type { Subscription } from '../types/subscription'
import { SubscriptionCard } from '../components/SubscriptionCard'
import { PlanTierPicker } from '../components/PlanTierPicker'

export function MyMembershipPage({ userId, refreshKey }: { userId: number; refreshKey: number }) {
  const [subscription, setSubscription] = useState<Subscription | null>(null)
  const [plans, setPlans] = useState<Plan[]>([])
  const [tiers, setTiers] = useState<Tier[]>([])
  const [pricing, setPricing] = useState<Price[]>([])
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(true)

  const load = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      const [plansRes, tiersRes, pricingRes] = await Promise.all([
        catalogApi.getPlans(),
        catalogApi.getTiers(),
        catalogApi.getPricing(),
      ])
      setPlans(plansRes)
      setTiers(tiersRes)
      setPricing(pricingRes)

      const current = await subscriptionApi.getCurrent(userId)
      setSubscription(current)
    } catch (err) {
      if (err instanceof ApiError && err.status === 404) {
        setSubscription(null)
      } else {
        setError(err instanceof Error ? err.message : String(err))
      }
    } finally {
      setLoading(false)
    }
  }, [userId])

  useEffect(() => {
    load()
  }, [load, refreshKey])

  async function handleChange(planId: number, tierId: number) {
    setError(null)
    try {
      await subscriptionApi.change(userId, planId, tierId)
      await load()
    } catch (err) {
      setError(err instanceof ApiError ? err.message : String(err))
    }
  }

  async function handleCancel() {
    setError(null)
    try {
      await subscriptionApi.cancel(userId)
      await load()
    } catch (err) {
      setError(err instanceof ApiError ? err.message : String(err))
    }
  }

  if (loading) return <p>Loading membership...</p>

  return (
    <section>
      <h2>My Membership</h2>
      {error && <p style={{ color: '#cf222e' }}>{error}</p>}

      {!subscription && <p>You don't have a membership yet. Head to Plans & Tiers to subscribe.</p>}

      {subscription && <SubscriptionCard subscription={subscription} onCancel={handleCancel} />}

      {subscription?.status === 'ACTIVE' && (
        <>
          <h3 style={{ marginTop: 24 }}>Change plan or tier</h3>
          <PlanTierPicker
            plans={plans}
            tiers={tiers}
            pricing={pricing}
            actionLabel="Switch"
            disabledCell={(planId, tierId) => planId === subscription.plan.id && tierId === subscription.tier.id}
            onSelect={handleChange}
          />
        </>
      )}
    </section>
  )
}
