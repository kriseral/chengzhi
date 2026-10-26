import { ref } from "vue";

const STORAGE_KEY = "chengzhi-session";
const session = ref(readSession());

function readSession() {
  const raw = localStorage.getItem(STORAGE_KEY);
  return raw ? JSON.parse(raw) : null;
}

export function useSession() {
  function save(value) {
    session.value = value;
    localStorage.setItem(STORAGE_KEY, JSON.stringify(value));
  }

  function clear() {
    session.value = null;
    localStorage.removeItem(STORAGE_KEY);
  }

  return { session, save, clear };
}
