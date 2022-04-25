<template>
  <div class="filename-menu-mask">
    <div class="filename-menu-wrapper">
      <div class="filename-menu-container">
        <div v-show="!isAddFormEnable" class="filename-menu">
          <div class="filename-menu-select">
            <span
              >{{ $filters.localize("namespace") }}
              <prompt :prompt="$filters.localize('prompt-namespace')"
                >&#128269;</prompt
              >
            </span>
            <div style="margin-bottom: 1em">
              <m-select
                :options="namespaces"
                :is-namespace="true"
                @selected="getSelectedNamespace"
                @deleted="deleteNamespace"
              ></m-select>
            </div>
            <div>
              <span>{{ $filters.localize("filename") }}</span>
            </div>
            <div style="margin-bottom: 1em">
              <m-select
                :options="filenames"
                :is-filename="true"
                :selected-namespace="selectedNamespace"
                @selected="getSelectedFilename"
                @deleted="deleteFilenameFromNamespace"
              ></m-select>
            </div>
          </div>
          <div>
            <button class="btn menu-btn">
              <label for="import">
                <span>{{ $filters.localize("import-data") }}</span>
                <input
                  type="file"
                  id="import"
                  ref="file"
                  style="display: none"
                  @change="importFromJson()"
                />
              </label>
            </button>
            <button class="btn menu-btn" @click="exportToJson()">
              <span>{{ $filters.localize("export-data") }}</span>
            </button>
          </div>
          <div>
            <button type="button" class="btn menu-btn" @click="getFilenames">
              {{ $filters.localize("download-db") }}
              <prompt :prompt="$filters.localize('prompt-download-db')"
                >&#128269;</prompt
              >
            </button>
            <button type="button" class="btn menu-btn" @click="showAddFrom">
              {{ $filters.localize("add-form") }}
              <prompt :prompt="$filters.localize('prompt-add-form')"
                >&#128269;</prompt
              >
            </button>
            <button type="button" class="btn menu-btn disable">{{ $filters.localize("delete-database") }}</button>
          </div>
          <div style="padding-top: 15px">
            <button
              type="button"
              class="btn cancel-btn"
              @click="$emit('close')"
            >
              {{ $filters.localize("btn-cancel") }}
            </button>
            <button type="button" class="btn submit-btn" @click="setFilename">
              {{ $filters.localize("btn-save") }}
            </button>
          </div>
        </div>
        <modal v-if="warn" :message="warnInfo" @close="closeWarn"></modal>
        <div v-if="isAddFormEnable" class="filename-menu">
          <add-form @namespace="addNamespace" :namespace="true">{{
            $filters.localize("add-namespace")
          }}</add-form>
          <add-form
            @filename="addFilename"
            :filename="true"
            :namespaces="namespaces"
            >{{ $filters.localize("add-filename") }}
          </add-form>
          <div class="close-btn">
            <button type="button" style="cursor: pointer" @click="closeAddForm">
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
import Modal from "../../components/Modal";
import { bearer } from "../../utils/bearer";
import { getUrl } from "../../utils/url";

export default {
  name: "FilenameMenu",
  emits: ["filename", "close"],
  components: { Modal, Prompt, AddForm, MSelect },
  data() {
    return {
      isAddFormEnable: false,
      namespaces: [],
      filenames: [],
      selectedFilename: "",
      selectedNamespace: "",
      map: new Map(),
      warn: false,
      warnInfo: "",
      getAllURL : "/naming/namespace-and-filenames",
      importURL : "/naming/import-from-json",
      exprotURL : "/naming/export-to-json"
    };
  },
  mounted() {
    this.getFilenames();
  },
  methods: {
    closeWarn() {
      this.warn = false;
    },
    closeAddForm() {
      this.isAddFormEnable = false;
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
        this.warnInfo = this.$filters.localize("warn-select-filename");
        this.warn = true;
      }
    },
    getSelectedNamespace(namespace) {
      if (this.map.has(namespace)) {
        this.selectedNamespace = namespace;
        this.filenames = this.map.get(namespace);
        this.selectedFilename = "";
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
      axios
        .get(getUrl(this.getAllURL), {
          headers: {
            "Access-Control-Expose-Headers": "Content-Disposition",
            Authorization: bearer(),
          },
        })
        .then((response) => {
          if (response.status === 200) {
            if (Object.entries(response.data).length === 0) {
              this.warnInfo = this.$filters.localize("warn-empty-db");
              this.warn = true;
              return;
            }
            for (const key of Object.keys(response.data)) {
              this.map.set(key, this.fillOptions(response.data[key]));
            }
            this.namespaces = this.fillOptions(Object.keys(response.data));
          }
        })
        .catch((error) => {
          if (!error.response) {
            this.warn = true;
            this.warnInfo = error.message;
          } else {
            this.warn = true;
            this.warnInfo = error.response.data;
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
    },
    importFromJson() {
      let formData = new FormData();
      formData.append("file", this.$refs.file.files[0]);
      axios
        .post(getUrl(this.importURL), formData, {
          headers: {
            "Content-Type": "multipart/addForm-data",
            Authorization: bearer(),
          },
        })
        .then((response) => {
          if (response.status === 200) {
            alert("Успешно!");
          }
        })
        .catch((error) => {
          alert(error);
        });
    },
    exportToJson() {
      axios
        .get(getUrl(this.exprotURL), {
          responseType: "arraybuffer",
          headers: {
            "Access-Control-Expose-Headers": "Content-Disposition",
            Authorization: bearer(),
          },
        })
        .then((response) => {
          if (response.status === 200) {
            const jsonBlob = new Blob([response.data], {
              type: "application/json",
            });
            const blobUrl = window.URL.createObjectURL(jsonBlob);
            const link = document.createElement("a");
            link.href = blobUrl;
            link.setAttribute("download", "export");
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
  },
};
</script>

<style scoped>
span,
label,
button {
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
  max-width: 900px;
  margin: 0 auto;
  padding: 20px 30px;
  background-color: lavender;
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
  border-radius: 0.1em;
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
  padding: 0.4em 0.8em;
  margin: 0.2em 0.1em;
  text-align: center;
  text-decoration: none;
  border: 0.1em solid black;
  border-radius: 0.1em;
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
  margin-top: 0.3em;
  margin-right: 0.3em;
  right: 0;
  top: 0;
}

.disable {
  cursor: not-allowed;
  opacity: 0.3;
}

.input-file-label {
  cursor: pointer;
  font-size: 12pt;
  font-weight: bold;
  background-color: white;
  color: black;
  text-align: center;
  text-decoration: none;
  border: 0.1em solid black;
  border-radius: 0.1em;
}

.input-file-label:hover {
  background-color: wheat;
}
</style>