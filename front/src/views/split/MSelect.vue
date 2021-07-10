<template>
    <div class="m-sel-container" :tabindex="0" @blur="blur" @focus="focus = true"
         :class="{'m-sel-m-sel-disabled' : true}">
        <div class="m-sel-value-container"
             :class="{'m-sel-value-container-focus' : focus & options.length !== 0,
                      'm-sel-value-container-empty' : options.length === 0}"
             @click="openCloseOptions">
            <div class="m-sel-value-div1">
                <span class="m-sel-value">{{selectedOption}}</span>
            </div>
            <div class="m-sel-value-div2">
                <span class="m-sel-arrow-down" v-if="!showOptions">&#8744;</span>
                <span class="m-sel-arrow-up" v-if="showOptions">&#8743;</span>
            </div>
        </div>
        <div class="m-sel-option-container">
            <ul class="m-sel-options" v-show="showOptions">
                <li class="m-sel-option" v-for="(option) in getOptions" :key="option">
                    <div class="m-sel-option-value" @click="selectOption(option)">
                        <span class="m-sel-option-span">{{option.value}}</span>
                    </div>
                    <div class="m-sel-option-menu" v-if="isDeleteEnable">
                        <div class="m-sel-confirm" v-if="option.canDelete">
                            <div>
                                <span class="m-sel-btn m-sel-btn-no" @click.stop="deleteOption(option)">YES</span>
                            </div>
                            <div>
                        <span class="m-sel-btn m-sel-btn-yes"
                              @click.stop="undoDelete(option)">UNDO</span>
                            </div>
                        </div>
                        <div class="m-sel-option-delete">
                            <div>
                                <span class="m-sel-btn" @click.stop="confirmDelete(option)">&#128465;</span>
                            </div>
                        </div>
                    </div>
                </li>
            </ul>
        </div>
    </div>
</template>

<script>
    import axios from "axios";

    export default {
        name: "m-select",
        emits: ["selected", "deleted"],
        props: {
            options: {
                type: Array,
                required: true,
            },
            isDeleteEnable: {
                type: Boolean,
                default: true
            },
            isNamespace: {
                type: Boolean,
                default: false,
            },
            isFilename: {
                type: Boolean,
                default: false
            },
        },
        watch: {
            options: {
                immediate: true,
                handler(cur, old) {
                    if (old !== cur) {
                        this.selectedOption = "Select option"
                    }
                }
            }
        },
        computed: {
            getOptions() {
                return this.options;
            }
        },
        data() {
            return {
                focus: false,
                showOptions: false,
                selectedOption: "Select option",
                status: "",
            };
        },
        methods: {
            confirmDelete(option) {
                if (option.canDelete) {
                    option.canDelete = !option.canDelete;
                } else {
                    option.canDelete = !option.canDelete;
                }

            },
            undoDelete(option) {
                option.canDelete = !option.canDelete;
            },
            deleteOption(option) {
                if (this.isNamespace) {
                    console.log("namespace will delete")
                    let URL = "http://192.168.3.2:6060/delete-namespace/".concat(option.value);
                    this.delete(URL, option);
                } else {
                    console.log("filename will delete")
                    let URL = "http://192.168.3.2:6060/delete-filename/".concat(option.value);
                    this.delete(URL, option);
                }
            },
            delete(URL, option) {
                axios.delete(URL, {
                    headers: {
                        'Access-Control-Allow-Origin': 'http://localhost:8080',
                    },
                }).then((response) => {
                    if (response.status === 200) {
                        this.selectedOption = "Select option";
                        this.status = "Success delete " + option.value;
                        this.$emit("deleted", option);
                    }
                }).catch(error => {
                    this.status = error.response.data.error + " " + error.response.status;
                });
            },
            selectOption(option) {
                this.showOptions = false;
                this.selectedOption = option.value;
                this.status = "";
                this.$emit("selected", this.selectedOption);
            },
            openCloseOptions() {
                this.showOptions = !this.showOptions;
                this.options.forEach(el => el.canDelete = false);
            },
            blur() {
                this.showOptions = false;
                this.focus = false;
            },
        }
    }
</script>

<style scoped>
    span, button {
        font-size: 12pt;
        font-family: Avenir, Helvetica, Arial, sans-serif;
        color: black;
    }

    .m-sel-value-container-empty {
        pointer-events: none;
        opacity: 0.5;
        z-index: 10;
    }

    .m-sel-value-container {
        display: flex;
        justify-content: space-between;
        align-items: center;
        width: 30em;
        min-height: 1.75em;
        background-color: #dddddd;
        border: .1em solid #aaa;
        box-shadow: 0 1px 0 1px rgba(0, 0, 0, .04);
        border-radius: .1em;
    }

    .m-sel-value-container-focus {
        border: .1em solid black;
        border-radius: .1em;
    }

    .m-sel-value {
        padding: 0 0 0 25px;
        cursor: pointer;
    }

    .m-sel-options {
        max-height: 15em;
        overflow-y: auto;
        position: absolute;
        max-width: 30em;
        width: 100%;
        margin: 0;
        padding: 0;
        border: .1em solid #aaa;
        border-radius: .1em;
        z-index: 999999;
    }

    .m-sel-option {
        display: flex;
        justify-content: space-between;
        width: 30em;
        background-color: #dddddd;
        border-radius: .1em;
        outline: .03em solid #aaa;
    }

    .m-sel-option-value {
        width: 100%;
        text-align: left;
    }

    .m-sel-option-delete {
        display: flex;
        justify-content: center;
        align-items: center;
        margin-right: 0.7em;
    }

    .m-sel-confirm {
        display: flex;
        justify-content: center;
        align-items: center;
        background-color: #dddddd;
    }

    .m-sel-btn {
        background-color: #ffffff;
        color: black;
        text-align: center;
        text-decoration: none;
        display: inline-block;
        margin: .1em .2em;
        padding: .1em;
        transition-duration: 0.4s;
        cursor: pointer;
        border: .1em solid black;
        border-radius: .1em;
        top: 50%;
    }

    .m-sel-option-menu {
        display: flex;
        justify-content: flex-end;
    }

    .m-sel-option-span {
        display: inline-block;
        word-break: break-all;

    }

    .m-sel-option-value:hover {
        cursor: pointer;
        background-color: #bebebe;
    }

    .m-sel-btn:hover {
        background-color: black;
    }

    .m-sel-btn-yes:hover {
        background-color: greenyellow;
    }

    .m-sel-btn-no:hover {
        background-color: red;
    }

    .m-sel-option-delete {

    }

    .m-sel-arrow-down {
        text-align: right;
        transition: 0.5s;
    }

    .m-sel-value-div1 {
        text-align: center;
        flex: 0 0 29em;
    }

    .m-sel-value-div2 {
        text-align: center;
        flex: 0 0 1em;
    }
</style>