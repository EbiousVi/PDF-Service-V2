<template>
    <div>
        <div class="header">
            <span class="service-name">PDF SERVICE</span><br/>
        </div>
        <div class="services">
            <div class="service" @click="$router.push('/split')"><span class="service-name">Split</span></div>
            <div class="service" @click="$router.push('/merge')"><span class="service-name">Merge</span></div>
        </div>
    </div>
</template>

<script>
    export default {
        name: "Main",
        components: {}
    }
</script>

<style scoped>
    .header {
        width: 100%;
        background-color: lavender;
        text-align: center;
        margin-bottom: 10px;

        border-radius: .2em;
    }

    .services {
        display: flex;
        flex-wrap: wrap;
        justify-content: center;
        align-items: center;
        height: 10em;
        border-radius: .3em;
    }

    .service {
        height: 100%;
        width: 48%;
        margin-right: .3em;
        padding-top: 5em;
        background-color: lavender;
        cursor: pointer;
        text-align: center;
        border-radius: .3em;
    }

    .service:hover {
        background-color: #d2c3ec;
    }

    .service-name {
        font-size: 24pt;
        font-family: Avenir, Helvetica, Arial, sans-serif;
        font-weight: bold;
    }
</style>