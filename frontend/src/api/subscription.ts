import { apiClient } from './client'
import type { Subscription, SubscriptionEvent } from '../types/subscription'

export const subscriptionApi = {
  getCurrent: (userId: number) => apiClient.get<Subscription>(`/users/${userId}/subscription`),
  subscribe: (userId: number, planId: number, tierId: number) =>
    apiClient.post<Subscription>(`/users/${userId}/subscription`, { planId, tierId }),
  change: (userId: number, planId?: number, tierId?: number) =>
    apiClient.patch<Subscription>(`/users/${userId}/subscription`, { planId, tierId }),
  cancel: (userId: number) => apiClient.delete<Subscription>(`/users/${userId}/subscription`),
  getEvents: (userId: number) => apiClient.get<SubscriptionEvent[]>(`/users/${userId}/subscription/events`),
}
