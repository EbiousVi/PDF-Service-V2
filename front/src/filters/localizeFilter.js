
import ru from '../locales/ru.json'

const locales = {
  'ru-RU': ru
}

export default function localizeFilter(key) {
  let locale = 'ru-RU'
  return locales[locale][key] || `[Localize error]: key ${key} not found`
}