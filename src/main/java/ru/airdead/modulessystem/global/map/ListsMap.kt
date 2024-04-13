package ru.airdead.modulessystem.global.map

class ListsMap<K, V> : HashMap<K, ArrayList<V>>() {

    /**
     * Добавляет элемент [value] в список по ключу [key]. Если список для ключа не существует,
     * он будет создан.
     */
    fun add(key: K, value: V) {
        this.computeIfAbsent(key) { ArrayList() }.add(value)
    }

    /**
     * Получает список по ключу [key]. Если список отсутствует, возвращает пустой список.
     * Не изменяет исходную карту.
     */
    fun getList(key: K): List<V> {
        return this[key] ?: emptyList()
    }

    /**
     * Удаляет элемент [value] из списка по ключу [key]. Если после удаления список пуст,
     * удаляет ключ из карты.
     */
    fun removeValue(key: K, value: V) {
        this[key]?.let {
            it.remove(value)
            if (it.isEmpty()) {
                this.remove(key)
            }
        }
    }

    /**
     * Получает все значения из всех списков в карте в виде одного списка.
     */
    fun allValues(): List<V> {
        return this.values.flatten()
    }
}
