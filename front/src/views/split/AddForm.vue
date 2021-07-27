<template>
    <div class="add-form-container">
        <div class="add-form-elem">
            <label>
                <span style="font-weight: bold;"><slot></slot>
                <prompt v-if="namespace"
                        prompt="Название должно быть уникальным. Минимальная длина названия пространства имен должна быть 3 символа">&#128269;</prompt>
                <prompt v-if="filename"
                        prompt="Имя файла должно быть уникальным в своем пространстве имен. Минимальная длина имени файла должна быть 3 символа. Не забудьте выбрать пространство имен, куда планируется сохранить имя файла">&#128269;</prompt>
                </span>
                <div style="display: flex; align-items: center">
                    <input v-if="namespace" type="text" v-model="form.namespace"
                           @input="isRespBad = false, isRespOk = false">
                    <input v-if="filename" type="text" v-model="form.filename"
                           @input="isRespBad = false, isRespOk = false">
                    <span v-if="isRespOk">&#9989;</span>
                    <span v-if="isRespBad">&#10060;
                        <prompt :prompt="badRespInfo">&#128269;</prompt>
                    </span>
                </div>
            </label>
        </div>
        <div class="add-form-elem" v-if="namespace">
            <button type="button" class="btn menu-btn" @click="addNamespace">
                <span style="word-break: break-all;">Add</span>
            </button>
        </div>
        <div v-if="filename" style="display: flex; flex-direction: column; align-items: center">
            <div class="add-form-elem">
                <m-select :options="namespaces" :is-delete-enable="false" @selected="getSelectedNamespace"></m-select>
            </div>
            <div class="add-form-elem">
                <button type="button" class="btn menu-btn" @click="addFilename">
                    <span style="word-break: break-all;">Add</span>
                </button>
            </div>
        </div>
    </div>
</template>

<script>
    import axios from "axios";
    import MSelect from "./MSelect";
    import Prompt from "../../components/Prompt";

    export default {
        name: "AddForm",
        components: {Prompt, MSelect},
        emits: ["filename", "namespace"],
        props: {
            namespaces: Array,
            namespace: {
                type: Boolean,
                default: false
            },
            filename: {
                type: Boolean,
                default: false
            }
        },
        data() {
            return {
                form: {
                    namespace: "",
                    filename: "",
                },
                badRespInfo: "",
                isRespOk: false,
                isRespBad: false,
                showAddForm: false,
            }
        },
        methods: {
            getSelectedNamespace(option) {
                this.form.namespace = option;
            },
            addFilename() {
                if (this.form.filename.length < 3) {
                    this.isRespBad = true;
                    this.badRespInfo = "Длина имени должна быть не меньше 3 символов";
                    return;
                }
                if (this.form.namespace.length === 0) {
                    this.isRespBad = true;
                    this.badRespInfo = "Пространство имен не выбрано";
                    return;
                }
                axios.post('http://192.168.3.2:6060/naming/add-filename', this.form)
                    .then(response => {
                        if (response.status === 200) {
                            this.isRespBad = false;
                            this.isRespOk = true;
                            this.$emit("filename", this.form)
                            this.form.filename = "";
                        }
                    }).catch(error => {
                    if (!error.response) {
                        this.isRespBad = true;
                        this.badRespInfo = error.message;
                    } else {
                        this.isRespBad = true;
                        this.badRespInfo = error.response.data;
                    }
                });
            },
            addNamespace() {
                if (this.form.namespace.length < 3) {
                    this.isRespBad = true;
                    this.badRespInfo = "Длина имени должна быть не меньше 3 символов";
                    return;
                }
                axios.get('http://192.168.3.2:6060/naming/add-namespace', {params: {namespace: this.form.namespace}})
                    .then(response => {
                        if (response.status === 200) {
                            this.isRespBad = false;
                            this.isRespOk = true;
                            this.$emit("namespace", this.form.namespace);
                            this.form.namespace = "";
                        }
                    }).catch(error => {
                    if (!error.response) {
                        this.isRespBad = true;
                        this.badRespInfo = error.message;
                    } else {
                        this.isRespBad = true;
                        this.badRespInfo = error.response.data;
                    }
                });
            },
        }
    }
</script>

<style scoped>
    span, label, button {
        font-size: 10pt;
        color: black;
    }

    input {
        display: block;
        width: 25em;
        padding: .6em 1.4em .5em .8em;
        background-color: rgba(211, 211, 211, 0.92);
        border: .1em solid #aaa;
        box-shadow: 0 1px 0 1px rgba(0, 0, 0, .04);
        border-radius: .1em;
    }

    .add-form-container {
        display: flex;
        flex-direction: column;
        align-items: center;
    }

    .add-form-elem {
        margin: .1em 0;
    }

    .btn {
        display: inline-block;
        background-color: #ffffff;
        color: black;
        padding: .4em .8em;
        margin: .2em .1em;
        text-align: center;
        text-decoration: none;
        border: .1em solid black;
        border-radius: .1em;
        cursor: pointer;
        transition-duration: 0.5s;
    }

    .menu-btn:hover {
        background-color: darkseagreen;
        color: black;
    }
</style>