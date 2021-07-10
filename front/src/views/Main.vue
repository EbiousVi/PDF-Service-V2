<template>
    <div>
        <div class="header">
            <span>PDF SERVICE</span><br/>
        </div>
        <div class="services">
            <div class="service" @click="$router.push('/split')"><span>Split</span></div>
            <div class="service" @click="$router.push('/merge')"><span>Merge</span></div>
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
    span {
        font-size: 24pt;
        font-weight: bold;
    }

    .header {
        width: 100%;
        background-color: antiquewhite;
        text-align: center;
        margin-bottom: 10px;
        border: .03em solid black;
        border-radius: .2em;
    }

    .services {
        display: flex;
        flex-wrap: wrap;
        justify-content: center;
        align-items: center;
        width: 100%;
        height: 10em;
        border: .03em solid black;
        border-radius: .2em;
    }

    .service {
        height: 100%;
        width: 50%;
        background-color: antiquewhite;
        outline: .03em solid black;
        cursor: pointer;
        text-align: center;
    }

    .service:hover {
        background-color: wheat;
    }
</style>