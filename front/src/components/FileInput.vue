<template>
    <div class="upload-file">
        <label class="input-file-label" title="UPLOAD FILE">
            <span v-if="!multiple">{{uploadFileName}}</span>
            <span v-if="multiple">{{uploadFilesName.join(", ")}}</span>
            <input type="file" id="file" ref="uploadFile" style="display:none;" accept="application/pdf"
                   @change="handleUploadFile" :multiple="multiple"/>
        </label>
        <div>
            <button class="btn" type="button" @click="reset"> &#128465;</button>
        </div>
    </div>
</template>

<script>
    export default {
        name: "FileInput",
        emits: ["file", "files", "reset"],
        props: {
            multiple: {
                type: Boolean,
                default: false,
            }
        },
        data() {
            return {
                uploadFile: "",
                uploadFileName: "SELECT PDF FILE",
                uploadFiles: [],
                uploadFilesName: ["SELECT PDF FILES"],
            }
        },
        methods: {
            handleUploadFile() {
                if (this.multiple) {
                    this.uploadFiles = this.$refs.uploadFile.files;
                    this.uploadFilesName = [];
                    for (let i = 0; i < this.$refs.uploadFile.files.length; i++) {
                        this.uploadFilesName.push(this.$refs.uploadFile.files[i].name)
                    }
                    this.$emit("files", this.uploadFiles);
                } else {
                    this.uploadFile = this.$refs.uploadFile.files[0];
                    this.uploadFileName = this.$refs.uploadFile.files[0].name;
                    this.$emit("file", this.uploadFile);
                }
            },
            reset() {
                //window.location.reload();
                if (this.multiple) {
                    this.$refs.uploadFile.value = '';
                    this.uploadFiles = [];
                    this.uploadFilesName = ["SELECT PDF FILES"];
                    this.$emit("reset");
                } else {
                    this.$refs.uploadFile.value = '';
                    this.uploadFile = "";
                    this.uploadFileName = 'SELECT PDF FILE';
                    this.$emit("reset");
                }
            }
        }
    }
</script>

<style scoped>
    span {
        word-break: break-word;
    }

    .upload-file {
        display: flex;
        justify-content: center;
    }

    .input-file-label {
        display: flex;
        justify-content: center;
        align-items: center;
        font-size: 18px;
        font-weight: bold;
        background-color: antiquewhite;
        color: black;
        min-height: 50px;
        min-width: 200px;
        border: 2px solid black;
        border-radius: 2px;
    }

    .input-file-label:hover {
        background-color: black;
        color: white;
        cursor: pointer;
    }

    .btn {
        background-color: #ffffff;
        color: black;
        padding: 10px 20px;
        text-align: center;
        text-decoration: none;
        display: inline-block;
        font-size: 16px;
        margin: 4px 2px;
        transition-duration: 0.4s;
        cursor: pointer;
        border: 2px solid black;
        border-radius: 2px;
    }

    .btn:hover {
        background-color: black;
        color: white;
    }
</style>