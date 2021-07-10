<template>
    <span v-bind:data-tooltip="prompt">
        <slot class="slot"></slot>
    </span>
</template>

<script>
    export default {
        name: "Prompt",
        props: {
            prompt: String,
        }
    }
</script>

<style scoped>
    span {
        cursor: help;
        font-size: 12pt;
    }

    [data-tooltip] {
        position: relative; /* Относительное позиционирование */
    }

    [data-tooltip]::after {
        content: attr(data-tooltip); /* Выводим текст */
        position: absolute; /* Абсолютное позиционирование */
        z-index: 999998;
        width: 300px; /* Ширина подсказки */
        right: 0;
        top: 1em; /* Положение подсказки */
        background: wheat; /* Синий цвет фона */
        color: black; /* Цвет текста */
        padding: 0.5em; /* Поля вокруг текста */
        box-shadow: 2px 2px 5px rgba(0, 0, 0, 0.3); /* Параметры тени */
        pointer-events: none; /* Подсказка */
        opacity: 0; /* Подсказка невидима */
        transition: 1s; /* Время появления подсказки */
        text-align: justify;
        font-size: 10pt;
        font-family: Avenir, Helvetica, Arial, sans-serif;
        word-break: break-word;
    }

    [data-tooltip]:hover::after {
        opacity: 1; /* Показываем подсказку */
        top: 2em; /* Положение подсказки */
    }
</style>