<template>
  <div class="merge-container">
    <button class="btn" style="margin-bottom: 10px" @click="$router.push('/')">
      {{ $filters.localize("main-btn") }}
    </button>
    <file-input
      @files="uploadFile"
      @reset="reset"
      :multiple="true"
    ></file-input>
    <waiting v-if="waiting"></waiting>
    <button v-if="isImgLoaded" class="btn" type="button" @click="merge">
      {{ $filters.localize("merge.merge-btn") }}
    </button>
    <modal v-if="alert" :message="alertInfo" @close="closeAlert"></modal>
    <cards v-if="isImgLoaded" :images="images" @order="getOrder"></cards>
  </div>
</template>

<script>
import FileInput from "../../components/FileInput";
import axios from "axios";
import Cards from "../../components/Cards";
import Waiting from "../../components/Waiting";
import Modal from "../../components/Modal";
import { bearer } from "../../utils/bearer";
import { getUrl } from "../../utils/url";

export default {
  name: "Merge",
  components: { Modal, Waiting, Cards, FileInput },
  data() {
    return {
      alert: false,
      alertInfo: "",
      waiting: false,
      images: [],
      isImgLoaded: false,
      order: [],
      mergeURL : "/merge-service/merge",
      uploaderPdfFilesURL : "/uploader/pdf-files",

    };
  },
  methods: {
    getOrder(order) {
      this.order = order;
    },
    reset() {
      this.isImgLoaded = false;
    },
    merge() {
      axios
        .post(getUrl(this.mergeURL), this.order, {
          responseType: "arraybuffer",
          headers: {
            "Access-Control-Expose-Headers": "Content-Disposition",
             Authorization: bearer()
          },
        })
        .then((response) => {
          if (response.status === 200) {
            const pdfBlob = new Blob([response.data], {
              type: "application/pdf",
            });
            const blobUrl = window.URL.createObjectURL(pdfBlob);
            const link = document.createElement("a");
            link.href = blobUrl;
            link.setAttribute("download", "merged");
            link.click();
            link.remove();
            URL.revokeObjectURL(blobUrl);
            this.order = [];
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
    uploadFile(files) {
      let formData = new FormData();
      for (let i = 0; i < files.length; i++) {
        formData.append("file", files[i]);
      }
      let promise = axios
        .post(getUrl(this.uploaderPdfFilesURL), formData, {
          headers: {
            "Content-Type": "multipart/addForm-data"
          },
        })
        .then((response) => {
          if (response.status === 200) {
            sessionStorage.setItem("token", response.data.token);
            const arr = [];
            for (let i = 0; i < response.data.covers.length; i++) {
              arr[i] = {
                filename: response.data.filenames[i],
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

.merge-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  background-color: lavender;
  width: 100%;
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

.btn:hover {
  background-color: black;
  color: white;
}
</style>