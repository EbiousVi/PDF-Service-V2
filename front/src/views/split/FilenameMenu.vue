<template>
    <div class="filename-menu-mask">
        <div class="filename-menu-wrapper">
            <div class="filename-menu-container">
                <div v-show="!isAddFormEnable" class="filename-menu">
                    <div class="filename-menu-select">
                        <span>Namespace
                        <prompt prompt="Пространство имен в котором содержатся имена файлов">&#128269;</prompt>
                        </span>
                        <div style="margin-bottom: 1em">
                            <m-select :options="namespaces" :is-namespace="true"
                                      @selected="getSelectedNamespace"
                                      @deleted="deleteNamespace"></m-select>
                        </div>
                        <div><span>Filename</span></div>
                        <div style="margin-bottom: 1em">
                            <m-select :options="filenames" :is-filename="true"
                                      @selected="getSelectedFilename"
                                      @deleted="deleteFilenameFromNamespace"></m-select>
                        </div>
                    </div>
                    <div><span style="color: red" v-if="warn">{{warnInfo}}</span></div>
                    <div>
                        <button type="button" class="btn menu-btn" @click="getFilenames">Get names
                            <prompt prompt="Загружает данные из БД">&#128269;</prompt>
                        </button>
                        <button type="button" class="btn menu-btn" @click="showAddFrom">Add Form
                            <prompt prompt="Добавление новых пространств имен и имен файлов">&#128269;</prompt>
                        </button>

                    </div>
                    <div class="buttons-line">
                        <button type="button" class="btn cancel-btn" @click="$emit('close')">
                            Cancel
                        </button>
                        <button type="button" class="btn submit-btn" @click="setFilename">
                            Download
                        </button>
                    </div>
                </div>

                <div v-if="isAddFormEnable" class="filename-menu">
                    <add-form @namespace="addNamespace" :namespace="true">Add Namespace</add-form>
                    <add-form @filename="addFilename" :filename="true" :namespaces="namespaces">Add filename to
                        namespace
                    </add-form>
                    <div class="close-btn">
                        <button type="button" style="cursor: pointer"
                                @click="closeAddForm">
                            &#10060;
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
    import axios from "axios";
    import MSelect from "./MSelect";
    import AddForm from "./AddForm";
    import Prompt from "../../components/Prompt";

    export default {
        name: "FilenameMenu",
        emits: ["filename", "close"],
        components: {Prompt, AddForm, MSelect},
        data() {
            return {
                isAddFormEnable: false,
                namespaces: [],
                filenames: [],
                selectedFilename: '',
                selectedNamespace: "",
                map: new Map,
                warn: false,
                warnInfo: "",
            }
        },
        mounted() {
            console.log("mounted filename-menu")
        },
        methods: {
            closeAddForm() {
                this.isAddFormEnable = false
            },
            showAddFrom() {
                this.warn = false;
                this.isAddFormEnable = true;
            },
            setFilename() {
                if (this.selectedFilename.length > 0) {
                    this.$emit("filename", this.selectedFilename);
                    this.warn = false;
                } else {
                    this.warnInfo = "Select filename!";
                    this.warn = true;
                }
            },
            getSelectedNamespace(namespace) {
                if (this.map.has(namespace)) {
                    this.selectedNamespace = namespace;
                    this.filenames = this.map.get(namespace);
                    this.selectedFilename = '';
                } else {
                    this.filenames = [];
                }
            },
            getSelectedFilename(filename) {
                this.selectedFilename = filename;
                this.warn = false;
            },
            addNamespace(namespace) {
                this.namespaces.push(this.fillOption(namespace));
                this.map.set(namespace, []);
            },
            addFilename(form) {
                console.log(form.namespace)
                this.map.get(form.namespace).push(this.fillOption(form.filename));
            },
            deleteNamespace(namespace) {
                this.namespaces.splice(this.namespaces.indexOf(namespace), 1);
                this.map.delete(namespace.value);
                if (this.selectedNamespace === namespace.value) {
                    this.selectedNamespace = "";
                    this.filenames = [];
                }
            },
            deleteFilenameFromNamespace(filename) {
                const index = this.filenames.indexOf(filename);
                this.map.get(this.selectedNamespace).splice(index, 1);
            },
            getFilenames() {
                axios.get('http://192.168.3.2:6060/get-filenames')
                    .then(response => {
                        if (response.status === 200) {
                            if (Object.entries(response.data).length === 0) {
                                this.warnInfo = "Пустая БД! Создайте пространства имен и заполните их именами файлов"
                                this.warn = true;
                                return;
                            }
                            for (const key of Object.keys(response.data)) {
                                this.map.set(key, this.fillOptions(response.data[key]));
                            }
                            this.namespaces = this.fillOptions(Object.keys(response.data));
                        }
                    });
            },
            fillOptions(arr) {
                const selectOptions = [];
                for (let i = 0; i < arr.length; i++) {
                    const fillOption = this.fillOption(arr[i]);
                    selectOptions.push(fillOption);
                }
                return selectOptions;
            },
            fillOption(value) {
                return {
                    value: value,
                    canDelete: false,
                };
            }
        },
    }
</script>

<style scoped>
    span, label, button {
        font-size: 10pt;
        font-weight: 700;
        font-family: Avenir, Helvetica, Arial, sans-serif;
        color: black;
    }

    .filename-menu-mask {
        position: fixed;
        z-index: 99999;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background-color: rgba(0, 0, 0, 0.5);
        display: table;
        transition: opacity 0.3s ease;
    }

    .filename-menu-wrapper {
        display: table-cell;
        vertical-align: middle;
    }

    .filename-menu-container {
        position: relative;
        max-width: 600px;
        margin: 0 auto;
        padding: 20px 30px;
        background-color: #fff;
        border-radius: 2px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.33);
        font-family: Helvetica, Arial, sans-serif;
        transition: opacity 0.3s ease;
    }

    .filename-menu {
        display: flex;
        flex-direction: column;
        align-items: center;
        background-color: lavender;
        border-radius: .1em;
    }

    .filename-menu-select {
        display: flex;
        flex-direction: column;
        align-items: center;
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
        background-color: wheat;
        color: black;
    }

    .submit-btn:hover {
        background-color: darkseagreen;
        color: black;
    }

    .cancel-btn:hover {
        background-color: red;
        color: black;
    }

    .close-btn {
        position: absolute;
        margin-top: 1.25em;
        margin-right: 1.85em;
        right: 0;
        top: 0;
    }
</style>