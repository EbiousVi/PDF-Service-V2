<template>
    <div class="split-container">
        <button class="btn" style="margin-bottom: 10px" @click="$router.push('/')">Main</button>
        <file-input @file="handleUploadFile" @reset="reset"></file-input>
        <div class="split-menu" v-if="isImgLoaded">
            <button class="btn" type="button" @click="simpleSplit">SPLIT</button>
            <button class="btn" type="button" @click="openFilenameMenu">SPLIT PRO
                <prompt prompt="Выбор имени для файла с разделенными страницами. Возможно добавить пространство имен для хранения определенных имен файлов. При добавление, всех сохраняется в базу данных.">
                    &#128269;
                </prompt>
            </button>
            <button class="btn" type="button" @click="splitAllByPage">SPLIT ALL
                <prompt prompt="Разделяет файл постранично, упаковывая в архив.">&#128269;</prompt>
            </button>
        </div>
        <modal v-if="alert" :message="alertInfo" @close="closeAlert"></modal>
        <div v-if="isSplitMenuEnable">
            <filename-menu v-show="isSplitMenuVisible" @close="closeFilenameMenu"
                           @filename="splitWithName"></filename-menu>
        </div>
        <pages v-if="isImgLoaded" :images="images" :isSuccessSplit="isSuccessSplit"
               @reset="isSuccessSplit = false" @selectedPages="getSelectedPages"></pages>
    </div>
</template>

<script>
    import axios from "axios";
    import Pages from "../../components/Pages";
    import FileInput from "../../components/FileInput";
    import FilenameMenu from "./FilenameMenu";
    import Modal from "../../components/Modal";
    import Prompt from "../../components/Prompt";

    export default {
        fileName: "UploadFile",
        components: {Prompt, Modal, FilenameMenu, FileInput, Pages},
        data() {
            return {
                alert: false,
                alertInfo: "",
                isSuccessSplit: false,
                isSplitMenuVisible: false,
                isSplitMenuEnable: false,
                images: [],
                isImgLoaded: false,
                selectedPages: [],
            }
        },
        methods: {
            splitAllByPage() {
                axios.get('http://192.168.3.2:6060/split-service/split-all', {
                    responseType: 'arraybuffer',
                    headers: {
                        "Access-Control-Expose-Headers": "Content-Disposition"
                    },
                }).then(response => {
                    if (response.status === 200) {
                        let zipBlob = new Blob([response.data], {type: "application/zip",});
                        const blobUrl = window.URL.createObjectURL(zipBlob);
                        const link = document.createElement('a');
                        //const fileName = response.headers["content-disposition"].match(/filename=(.*)/);
                        link.href = blobUrl;
                        link.setAttribute('download', "pages");
                        link.click();
                        link.remove();
                        URL.revokeObjectURL(blobUrl);
                    }
                });
            },
            splitWithName(filename) {
                this.isSplitMenuVisible = false;
                this.postSelectedPages(filename);
            },
            simpleSplit() {
                if (this.selectedPages.length === 0) {
                    this.alertInfo = "Choose pages to Split!";
                    this.alert = true;
                } else {
                    const filename = "pages_".concat(this.selectedPages);
                    this.postSelectedPages(filename);
                }
            },
            postSelectedPages(filename) {
                axios.post('http://192.168.3.2:6060/split-service/split-by-selected-pages', this.selectedPages,
                    {
                        responseType: 'arraybuffer',
                        headers: {
                            "Access-Control-Expose-Headers": "Content-Disposition"
                        },
                    }).then(response => {
                    if (response.status === 200) {
                        const pdfBlob = new Blob([response.data], {type: "application/pdf"});
                        const blobUrl = window.URL.createObjectURL(pdfBlob);
                        const link = document.createElement('a');
                        link.href = blobUrl;
                        link.setAttribute('download', filename);
                        link.click();
                        link.remove();
                        URL.revokeObjectURL(blobUrl);
                        this.isSuccessSplit = true;
                        this.selectedPages = [];
                    }
                });
            },
            openFilenameMenu() {
                if (this.selectedPages.length === 0) {
                    this.alertInfo = "Choose pages to Split!";
                    this.alert = true;
                } else {
                    this.isSplitMenuEnable = true;
                    this.isSplitMenuVisible = true;
                }
            },
            closeFilenameMenu() {
                this.isSplitMenuVisible = false
            },
            handleUploadFile(file) {
                this.uploadFile(file);
            },
            uploadFile(file) {
                let formData = new FormData();
                formData.append('file', file);
                axios.post('http://192.168.3.2:6060/split-service/upload', formData,
                    {
                        headers: {
                            'Content-Type': 'multipart/addForm-data'
                        }
                    }
                ).then(response => {
                    if (response.status === 200) {
                        const arr = [];
                        for (let i = 0; i < response.data.length; i++) {
                            arr[i] = {
                                number: i,
                                imgLink: response.data[i],
                                drag: false,
                                drop: false,
                            }
                        }
                        this.images = arr;
                        this.isImgLoaded = true;
                    }
                });
            },
            getSelectedPages(selectedPages) {
                this.selectedPages = selectedPages;
            },
            reset() {
                this.isImgLoaded = false;
            },
            closeAlert() {
                this.alert = !this.alert;
            },
        }
    }
</script>

<style scoped>
    span, label, button {
        font-size: 12pt;
        color: black;
    }

    .split-container {
        display: flex;
        flex-direction: column;
        align-items: center;
        background-color: lavender;
    }

    .split-menu {
        display: flex;
        justify-content: center;
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

    .btn:hover {
        background-color: black;
        color: white;
    }
</style>