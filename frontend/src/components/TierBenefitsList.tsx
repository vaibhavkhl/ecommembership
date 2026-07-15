import type { Benefit } from '../types/catalog'

function describe(benefit: Benefit): string {
  if (benefit.code === 'DISCOUNT_PERCENT' && benefit.configValue) {
    return `${benefit.description} (${benefit.configValue}%)`
  }
  return benefit.description
}

export function TierBenefitsList({ benefits }: { benefits: Benefit[] }) {
  return (
    <ul style={{ margin: 0, paddingLeft: 18 }}>
      {benefits.map((benefit) => (
        <li key={benefit.code}>{describe(benefit)}</li>
      ))}
    </ul>
  )
}
