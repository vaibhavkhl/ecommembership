import type { Benefit } from '../types/catalog'

function describe(benefit: Benefit): string {
  if (benefit.code === 'DISCOUNT_PERCENT' && benefit.configValue) {
    return `${benefit.description} (${benefit.configValue}%)`
  }
  return benefit.description
}

export function TierBenefitsList({ benefits }: { benefits: Benefit[] }) {
  if (benefits.length === 0) {
    return <p className="text-sm text-slate-500 dark:text-slate-400">No benefits configured.</p>
  }

  return (
    <ul className="space-y-1.5">
      {benefits.map((benefit) => (
        <li key={benefit.code} className="flex items-start gap-2 text-sm text-slate-700 dark:text-slate-300">
          <span className="mt-1 h-1.5 w-1.5 shrink-0 rounded-full bg-indigo-500" />
          {describe(benefit)}
        </li>
      ))}
    </ul>
  )
}
