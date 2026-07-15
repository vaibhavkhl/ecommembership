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
    <table style={{ borderCollapse: 'collapse', width: '100%' }}>
      <thead>
        <tr>
          <th style={{ textAlign: 'left', padding: 8 }}>Tier</th>
          {plans.map((plan) => (
            <th key={plan.id} style={{ textAlign: 'left', padding: 8 }}>
              {plan.code}
            </th>
          ))}
        </tr>
      </thead>
      <tbody>
        {tiers.map((tier) => (
          <tr key={tier.id} style={{ borderTop: '1px solid #d0d7de' }}>
            <td style={{ padding: 8, fontWeight: 600 }}>{tier.code}</td>
            {plans.map((plan) => {
              const price = priceFor(plan.id, tier.id)
              const disabled = disabledCell?.(plan.id, tier.id) ?? false
              return (
                <td key={plan.id} style={{ padding: 8 }}>
                  {price && (
                    <div>
                      <div>
                        {price.currency} {price.price}
                      </div>
                      <button disabled={disabled} onClick={() => onSelect(plan.id, tier.id)}>
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
  )
}
