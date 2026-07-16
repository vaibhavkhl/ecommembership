import { useCallback, useEffect, useState } from 'react'
import { catalogApi } from '../api/catalog'
import { subscriptionApi } from '../api/subscription'
import { ApiError } from '../api/client'
import type { Plan, Tier, Price } from '../types/catalog'
import type { Subscription, SubscriptionEvent } from '../types/subscription'
import { SubscriptionCard } from '../components/SubscriptionCard'
import { PlanTierPicker } from '../components/PlanTierPicker'
import { SubscriptionHistory } from '../components/SubscriptionHistory'

export function MyMembershipPage({ userId, refreshKey }: { userId: number; refreshKey: number }) {
  const [subscription, setSubscription] = useState<Subscription | null>(null)
  const [events, setEvents] = useState<SubscriptionEvent[]>([])
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
      setEvents(await subscriptionApi.getEvents(userId))
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

  if (loading) return <p className="text-slate-500 dark:text-slate-400">Loading membership...</p>

  return (
    <section className="space-y-8">
      <div>
        <h2 className="mb-3 text-xl font-semibold text-slate-900 dark:text-slate-100">My Membership</h2>
        {error && <p className="mb-3 text-sm text-red-600 dark:text-red-400">{error}</p>}

        {!subscription && (
          <p className="text-slate-600 dark:text-slate-400">
            You don&apos;t have a membership yet. Head to Plans &amp; Tiers to subscribe.
          </p>
        )}

        {subscription && <SubscriptionCard subscription={subscription} onCancel={handleCancel} />}
      </div>

      {subscription?.status === 'ACTIVE' && (
        <div>
          <h3 className="mb-3 text-lg font-semibold text-slate-900 dark:text-slate-100">Change plan or tier</h3>
          <PlanTierPicker
            plans={plans}
            tiers={tiers}
            pricing={pricing}
            actionLabel="Switch"
            disabledCell={(planId, tierId) => planId === subscription.plan.id && tierId === subscription.tier.id}
            onSelect={handleChange}
          />
        </div>
      )}

      {events.length > 0 && (
        <div>
          <h3 className="mb-3 text-lg font-semibold text-slate-900 dark:text-slate-100">History</h3>
          <SubscriptionHistory events={events} />
        </div>
      )}
    </section>
  )
}
