import { useCallback, useEffect, useState } from 'react'
import { catalogApi } from '../api/catalog'
import { adminApi } from '../api/admin'
import type { Tier } from '../types/catalog'
import type { BenefitDefinition, TierBenefit, TierCriteria } from '../types/admin'
import { BenefitDefinitionsPanel } from '../components/admin/BenefitDefinitionsPanel'
import { TierBenefitsPanel } from '../components/admin/TierBenefitsPanel'
import { TierCriteriaPanel } from '../components/admin/TierCriteriaPanel'

export function AdminPage() {
  const [tiers, setTiers] = useState<Tier[]>([])
  const [benefitDefinitions, setBenefitDefinitions] = useState<BenefitDefinition[]>([])
  const [tierBenefits, setTierBenefits] = useState<TierBenefit[]>([])
  const [tierCriteria, setTierCriteria] = useState<TierCriteria[]>([])
  const [loading, setLoading] = useState(true)

  const load = useCallback(async () => {
    const [tiersRes, defsRes, tierBenefitsRes, criteriaRes] = await Promise.all([
      catalogApi.getTiers(),
      adminApi.listBenefitDefinitions(),
      adminApi.listTierBenefits(),
      adminApi.listTierCriteria(),
    ])
    setTiers(tiersRes)
    setBenefitDefinitions(defsRes)
    setTierBenefits(tierBenefitsRes)
    setTierCriteria(criteriaRes)
    setLoading(false)
  }, [])

  useEffect(() => {
    load()
  }, [load])

  if (loading) return <p className="text-slate-500 dark:text-slate-400">Loading admin data...</p>

  return (
    <section className="space-y-6">
      <div>
        <h2 className="text-xl font-semibold text-slate-900 dark:text-slate-100">Admin</h2>
        <p className="text-sm text-slate-500 dark:text-slate-400">
          Configure membership benefits and tier auto-qualification criteria.
        </p>
      </div>

      <BenefitDefinitionsPanel definitions={benefitDefinitions} onCreated={load} />
      <TierBenefitsPanel tiers={tiers} benefitDefinitions={benefitDefinitions} tierBenefits={tierBenefits} onChanged={load} />
      <TierCriteriaPanel tiers={tiers} criteria={tierCriteria} onChanged={load} />
    </section>
  )
}
