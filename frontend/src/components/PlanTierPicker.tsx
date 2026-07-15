import type { Plan, Tier, Price } from '../types/catalog'

interface Props {
  plans: Plan[]
  tiers: Tier[]
  pricing: Price[]
  actionLabel: string
  disabledCell?: (planId: number, tierId: number) => boolean
  onSelect: (planId: number, tierId: number) => void
}

export function PlanTierPicker({ plans, tiers, pricing, actionLabel, disabledCell, onSelect }: Props) {
  const priceFor = (planId: number, tierId: number) =>
    pricing.find((p) => p.planId === planId && p.tierId === tierId)

  return (
    <div className="overflow-x-auto rounded-lg border border-slate-200 dark:border-slate-700">
      <table className="w-full border-collapse text-left text-sm">
        <thead className="bg-slate-50 dark:bg-slate-800">
          <tr>
            <th className="px-4 py-3 font-semibold text-slate-600 dark:text-slate-300">Tier</th>
            {plans.map((plan) => (
              <th key={plan.id} className="px-4 py-3 font-semibold text-slate-600 dark:text-slate-300">
                {plan.code}
              </th>
            ))}
          </tr>
        </thead>
        <tbody className="divide-y divide-slate-200 dark:divide-slate-700">
          {tiers.map((tier) => (
            <tr key={tier.id}>
              <td className="px-4 py-3 font-medium text-slate-900 dark:text-slate-100">{tier.code}</td>
              {plans.map((plan) => {
                const price = priceFor(plan.id, tier.id)
                const disabled = disabledCell?.(plan.id, tier.id) ?? false
                return (
                  <td key={plan.id} className="px-4 py-3">
                    {price && (
                      <div className="flex items-center gap-3">
                        <span className="text-slate-700 dark:text-slate-300">
                          {price.currency} {price.price}
                        </span>
                        <button
                          disabled={disabled}
                          onClick={() => onSelect(plan.id, tier.id)}
                          className="rounded-md bg-indigo-600 px-2.5 py-1 text-xs font-semibold text-white transition hover:bg-indigo-500 disabled:cursor-not-allowed disabled:bg-slate-300 disabled:text-slate-500 dark:disabled:bg-slate-700 dark:disabled:text-slate-400"
                        >
                          {disabled ? 'Current' : actionLabel}
                        </button>
                      </div>
                    )}
                  </td>
                )
              })}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
