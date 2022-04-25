<template>
  <div class="split-container">
    <button class="btn" style="margin-bottom: 10px" @click="$router.push('/')">
      {{ $filters.localize("main-btn") }}
    </button>
    <file-input @file="handleUploadFile" @reset="reset"></file-input>
    <waiting v-if="waiting"></waiting>
    <div class="split-menu" v-if="isImgLoaded">
      <button class="btn split-btn" type="button" @click="simpleSplit">
        {{ $filters.localize("split.split-btn") }}
      </button>
      <button class="btn split-btn" type="button" @click="openFilenameMenu">
        {{ $filters.localize("split.split-naming-btn") }}
        <prompt :prompt="$filters.localize('prompt-split-pro')">
          &#128269;
        </prompt>
      </button>
      <button class="btn split-btn" type="button" @click="splitBySinglePages">
        {{ $filters.localize("split.split-by-pages-btn") }}
        <prompt :prompt="$filters.localize('prompt-split-all')"
          >&#128269;</prompt
        >
      </button>
    </div>
    <modal v-if="alert" :message="alertInfo" @close="closeAlert"></modal>
    <div v-if="isSplitMenuEnable">
      <filename-menu
        v-show="isSplitMenuVisible"
        @close="closeFilenameMenu"
        @filename="splitWithName"
      ></filename-menu>
    </div>
    <modal v-if="alert" :message="alertInfo" @close="closeAlert"></modal>
    <pages
      v-if="isImgLoaded"
      :images="images"
      :isSuccessSplit="isSuccessSplit"
      @reset="isSuccessSplit = false"
      @selectedPages="getSelectedPages"
    ></pages>
  </div>
</template>

<script>
import { bearer } from "../../utils/bearer";
import { getUrl } from "../../utils/url";
import axios from "axios";
import Pages from "../../components/Pages";
import FileInput from "../../components/FileInput";
import FilenameMenu from "./FilenameMenu";
import Modal from "../../components/Modal";
import Prompt from "../../components/Prompt";
import Waiting from "../../components/Waiting";

export default {
  fileName: "UploadFile",
  components: {
    Waiting,
    Prompt,
    Modal,
    FilenameMenu,
    FileInput,
    Pages,
  },
  data() {
    return {
      waiting: false,
      alert: false,
      alertInfo: '',
      isSuccessSplit: false,
      isSplitMenuVisible: false,
      isSplitMenuEnable: false,
      images: [],
      isImgLoaded: false,
      selectedPages: [],
      uploadPdfFileURL : '/uploader/pdf-file',
      splitBySinglePagesURL : '/split-service/split-by-single-pages',
      splitBySelectedPagesURL : '/split-service/split-by-selected-pages',
    };
  },
  methods: {
    splitBySinglePages() {
      axios
        .get(getUrl(this.splitBySinglePagesURL), {
          responseType: "arraybuffer",
          headers: {
            "Access-Control-Expose-Headers": "Content-Disposition",
            Authorization: bearer(),
          },
        })
        .then((response) => {
          if (response.status === 200) {
            let zipBlob = new Blob([response.data], {
              type: "application/zip",
            });
            const blobUrl = window.URL.createObjectURL(zipBlob);
            const link = document.createElement("a");
            //const fileName = response.headers["content-disposition"].match(/filename=(.*)/);
            link.href = blobUrl;
            link.setAttribute("download", "pages");
            link.click();
            link.remove();
            URL.revokeObjectURL(blobUrl);
          }
        })
        .catch((error) => {
          if (!error.response) {
            this.alert = true;
            this.alertInfo = error.message;
          } else {
            this.alert = true;
            this.alertInfo = error.response.data;
          }
        });
    },
    splitWithName(filename) {
      this.isSplitMenuVisible = false;
      this.postSelectedPages(filename);
    },
    simpleSplit() {
      if (this.selectedPages.length === 0) {
        this.alertInfo = this.$filters.localize("alert-selected-pages");
        this.alert = true;
      } else {
        const filename = "pages_".concat(this.selectedPages);
        this.postSelectedPages(filename);
      }
    },
    postSelectedPages(filename) {
      axios
        .post(
          getUrl(this.splitBySelectedPagesURL),
          this.selectedPages,
          {
            responseType: "arraybuffer",
            headers: {
              "Access-Control-Expose-Headers": "Content-Disposition",
              Authorization: bearer()
            },
          }
        )
        .then((response) => {
          if (response.status === 200) {
            const pdfBlob = new Blob([response.data], {
              type: "application/pdf",
            });
            const blobUrl = window.URL.createObjectURL(pdfBlob);
            const link = document.createElement("a");
            link.href = blobUrl;
            link.setAttribute("download", filename);
            link.click();
            link.remove();
            URL.revokeObjectURL(blobUrl);
            this.isSuccessSplit = true;
            this.selectedPages = [];
          }
        })
        .catch((error) => {
          if (!error.response) {
            this.alert = true;
            this.alertInfo = error.message;
          } else {
            this.alert = true;
            this.alertInfo = error.response.data;
          }
        });
    },
    openFilenameMenu() {
      if (this.selectedPages.length === 0) {
        this.alertInfo = this.$filters.localize("alert-selected-pages");
        this.alert = true;
      } else {
        this.isSplitMenuEnable = true;
        this.isSplitMenuVisible = true;
      }
    },
    closeFilenameMenu() {
      this.isSplitMenuVisible = false;
    },
    handleUploadFile(file) {
      this.uploadFile(file);
    },
    uploadFile(file) {
      let formData = new FormData();
      formData.append("file", file);
      let promise = axios
        .post(getUrl(this.uploadPdfFileURL), formData, {
          headers: {
            "Content-Type": "multipart/addForm-data",
          },
        })
        .then((response) => {
          if (response.status === 200) {
            sessionStorage.setItem("token", response.data.token);
            const arr = [];
            for (let i = 0; i < response.data.covers.length; i++) {
              arr[i] = {
                number: i,
                imgLink: response.data.covers[i],
                drag: false,
                drop: false,
                full: false,
              };
            }
            this.images = arr;
            this.isImgLoaded = true;
            this.waiting = false;
          }
        })
        .catch((error) => {
          if (!error.response) {
            this.alert = true;
            this.alertInfo = error.message;
          } else {
            this.alert = true;
            this.alertInfo = error.response.data;
          }
          this.waiting = false;
        });
      promise.then((this.waiting = true));
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
  },
};
</script>

<style scoped>
span,
label,
button {
  font-size: 12pt;
  color: black;
}

.split-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  background-color: lavender;
  border-radius: 0.1em;
}

.split-menu {
  display: flex;
  justify-content: center;
}

.btn {
  display: inline-block;
  background-color: #ffffff;
  color: black;
  padding: 0.4em 0.8em;
  margin: 0.2em 0.1em;
  text-align: center;
  text-decoration: none;
  border: 0.1em solid black;
  border-radius: 0.1em;
  cursor: pointer;
  transition-duration: 0.5s;
}

.split-btn {
  background-color: plum;
}

.btn:hover {
  background-color: black;
  color: white;
}
</style>