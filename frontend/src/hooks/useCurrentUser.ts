import { useState } from 'react'

const DEMO_USER_ID = 1

export function useCurrentUser() {
  const [userId, setUserId] = useState(DEMO_USER_ID)
  return { userId, setUserId }
}
