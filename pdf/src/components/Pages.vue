<template>
  <div class="pages">
    <div class="control-panel">
      <div class="page-options-btn">
        <button class="btn" type="button" @click="selectAll">
          {{ $filters.localize("btn-select-all") }}
        </button>
        <button class="btn" type="button" @click="reset">
          {{ $filters.localize("btn-reset") }}
        </button>
        <button
          class="btn"
          :class="{ 'enable-btn': isOrderLocked }"
          type="button"
          @click="lockSort"
        >
          {{ $filters.localize("btn-order-lock") }}
          <prompt :prompt="$filters.localize('prompt-order')">&#128269;</prompt>
        </button>
        <button
          class="btn"
          :class="{ 'enable-btn': isDeleteEnable }"
          @click="enableDelete"
        >
          {{ $filters.localize("btn-delete-after") }}
          <prompt :prompt="$filters.localize('prompt-delete')"
            >&#128269;</prompt
          >
        </button>
      </div>
      <div class="page-selected">
        {{ $filters.localize("selected-pages") }}:
        {{ selectedPages.join(", ") }}
      </div>
    </div>
    <div class="pages-board">
      <button v-if="isRtnBtnEnable" @click="returnPages" class="btn">
        {{ $filters.localize("btn-return") }}
      </button>
      <div
        v-for="page in pages"
        :key="page"
        class="page"
        :class="{
          'page-drag': page.drag,
          'page-drop': page.drop,
          'page-static': !page.drop,
        }"
      >
        <div>
          <label class="page-checkbox">
            <input
              v-model="selectedPages"
              :value="page.number"
              type="checkbox"
              class="page-check-input"
              required
            />
          </label>
        </div>
        <div class="page-number">{{ page.number }}</div>
        <div class="page-delete">
          <button
            @click="deletePage(page)"
            class="delete-btn"
            type="button"
            title="DELETE"
          >
            &#10060;
          </button>
        </div>
        <div>
          <img
            class="page-img"
            :draggable="isOrderLocked"
            @dragstart="dragStart($event, page)"
            @dragend="dragEnd(page)"
            @dragenter="dragEnter(page)"
            @dragleave="dragLeave(page)"
            @drop="onDrop($event, page)"
            @dragover.prevent
            @dragenter.prevent
            @click="selectPage(page.number)"
            @contextmenu.prevent="page.full = true"
            :src="page.imgLink"
            :alt="page.imgLink"
          />
        </div>
        <modal-image
          v-if="page.full"
          :image="page.imgLink"
          @close="page.full = false"
        ></modal-image>
      </div>
    </div>
  </div>
</template>

<script>
import Prompt from "./Prompt";
import ModalImage from "./ModalImage.vue";

export default {
  components: { Prompt, ModalImage },
  fileName: "Pages",
  props: {
    images: Array,
    isSuccessSplit: Boolean,
  },
  emits: ["selectedPages", "reset"],
  data() {
    return {
      orderPrompt: this.$filters.localize("prompt-order"),
      deletePrompt: this.$filters.localize("prompt-delete"),
      isDeleteEnable: true,
      isOrderLocked: false,
      isRtnBtnEnable: false,
      allSelected: false,
      pages: this.images,
      initialPages: [],
      selectedPages: [],
    };
  },
  watch: {
    images: {
      immediate: true,
      handler(cur, old) {
        if (old === undefined) {
          this.initialPages = this.pages.slice();
          return;
        }
        if (old !== cur) {
          this.pages = cur;
        }
        if (old === cur) {
          this.pages = cur;
        }
        this.initialPages = this.pages.slice();
      },
    },
  },
  updated() {
    if (this.isSuccessSplit && this.isDeleteEnable) {
      this.$emit("reset");
      this.pages = this.pages.filter(
        (el) => !this.selectedPages.includes(el.number)
      );
      this.selectedPages = [];
      if (this.pages.length === 0) {
        this.pages = this.initialPages;
        this.initialPages = this.initialPages.slice();
      }
    } else if (this.isSuccessSplit) {
      this.$emit("reset");
      this.selectedPages = [];
    }
  },
  methods: {
    dragEnd(page) {
      //Источник перетаскивания получит событие dragEnd, когда перетаскивание завершится, было оно удачным или нет
      const index = this.pages.indexOf(page);
      this.pages[index].drag = false;
    },
    dragEnter(page) {
      //Срабатывает, когда перемещаемый элемент попадает на элемент-назначение
      const index = this.pages.indexOf(page);
      this.pages[index].drop = true;
    },
    dragLeave(page) {
      const index = this.pages.indexOf(page);
      this.pages[index].drop = false;
    },
    dragStart(evt, page) {
      //Срабатывает когда элемент начал перемещаться
      const index = this.pages.indexOf(page);
      this.pages[index].drag = true;
      evt.dataTransfer.dropEffect = "move";
      evt.dataTransfer.effectAllowed = "move";
      evt.dataTransfer.setData("pageNum", page.number);
    },
    onDrop(evt, currentPage) {
      //вызывается для элемента, над которым произошло "сбрасывание" перемещаемого элемента.
      const dragPageNumber = evt.dataTransfer.getData("pageNum");
      const dragPage = this.pages.find(
        (page) => page.number === parseInt(dragPageNumber)
      );
      const dragPageIndex = this.pages.indexOf(dragPage);
      const currentPageIndex = this.pages.indexOf(currentPage);
      //swap
      setTimeout(() => {
        this.pages[dragPageIndex] = currentPage;
        this.pages[currentPageIndex] = dragPage;
      });
      //disable all
      this.pages[currentPageIndex].drop = false;
      this.pages[dragPageIndex].drop = false;
      this.pages[currentPageIndex].drag = false;
      this.pages[dragPageIndex].drag = false;
      //swap checkbox
      const dragPageNumberIndex = this.selectedPages.indexOf(dragPage.number);
      const currentPageNumberIndex = this.selectedPages.indexOf(
        currentPage.number
      );
      if (dragPageNumberIndex === -1) {
        this.selectedPages[currentPageNumberIndex] = dragPage.number;
      } else if (currentPageNumberIndex === -1) {
        this.selectedPages[dragPageNumberIndex] = currentPage.number;
      } else if (dragPageNumberIndex !== -1 && currentPageNumberIndex !== -1) {
        this.selectedPages[dragPageNumberIndex] = currentPage.number;
        this.selectedPages[currentPageNumberIndex] = dragPage.number;
      }
    },
    selectPage(page) {
      const index = this.selectedPages.indexOf(page);
      if (index === -1) {
        this.selectedPages.push(parseInt(page));
      } else {
        this.selectedPages.splice(index, 1);
      }
      if (!this.isOrderLocked) {
        this.selectedPages.sort((x, y) => x - y);
      }
      this.$emit("selectedPages", this.selectedPages);
    },
    deletePage(page) {
      const selectedIndex = this.selectedPages.indexOf(page.number);
      this.selectedPages.splice(selectedIndex, 1);
      const pageIndex = this.pages.indexOf(page);
      this.pages.splice(pageIndex, 1);
      this.$emit("selectedPages", this.selectedPages);
      if (this.pages.length === 0) {
        this.isRtnBtnEnable = true;
      }
    },
    returnPages() {
      this.pages = this.initialPages;
      this.initialPages = this.initialPages.slice();
      this.isRtnBtnEnable = false;
    },
    selectAll() {
      if (this.selectedPages.length === this.pages.length) {
        this.allSelected = true;
      }
      if (!this.allSelected) {
        this.allSelected = !this.allSelected;
        this.selectedPages = [];
        this.selectedPages = this.pages.map((page) => page.number);
      } else {
        this.allSelected = !this.allSelected;
        this.selectedPages = [];
      }
      this.$emit("selectedPages", this.selectedPages);
    },
    reset() {
      if (this.isOrderLocked) {
        this.pages.sort((x, y) => x.number - y.number);
      }
      this.selectedPages = [];
      this.allSelected = false;
      this.pages = this.initialPages;
      this.initialPages = this.initialPages.slice();
      this.$emit("selectedPages", this.selectedPages);
    },
    lockSort() {
      if (!this.isOrderLocked) {
        this.isOrderLocked = !this.isOrderLocked;
      } else {
        this.pages.sort((x, y) => x.number - y.number);
        this.selectedPages.sort((x, y) => x - y);
        this.isOrderLocked = !this.isOrderLocked;
      }
    },
    enableDelete() {
      if (this.isDeleteEnable) {
        this.isDeleteEnable = !this.isDeleteEnable;
      } else {
        this.isDeleteEnable = !this.isDeleteEnable;
      }
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
.pages {
  display: flex;
  flex-direction: column;
  align-content: center;
  margin: 0 5em 0 5em;
  width: 100%;
}

.pages-board {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  background-color: lightgray;
  border-radius: 0.2em;
  width: 100%;
}

.page-static {
  border: 2px solid black;
  border-radius: 4px;
}

.page-drag {
  border: 2px dashed blue;
  border-radius: 4px;
  opacity: 0.6;
}

.page-drop {
  transition: 0.25s;
  border: 2px inset blue;
  border-radius: 4px;
  -moz-box-shadow: 0 0 10px blue;
  -webkit-box-shadow: 0 0 10px blue;
  box-shadow: 0 0 10px blue;
}

.page {
  width: 170px;
  height: 250px;
  position: relative;
  margin-left: 10px;
  margin-right: 10px;
  margin-bottom: 5px;
}

.page:hover .page-number {
  background-color: wheat;
  font-size: 22px;
  font-weight: bold;
  transition: 500ms;
}

.page-number {
  position: absolute;
  left: 0;
  bottom: 0;
  width: 25px;
  height: 25px;
  background-color: lightgray;
  color: black;
  text-align: center;
  font-weight: bold;
  border: 1px solid black;
  border-radius: 1px;
  transition: 500ms;
}

.page-img {
  width: 170px;
  height: 250px;
  border-radius: 2px;
}

.page-delete {
  position: absolute;
  right: 0;
  top: 0;
}

.page-checkbox {
  position: absolute;
  left: 0;
}

.page-check-input {
  width: 25px;
  height: 25px;
}

.control-panel {
  width: 100%;
  border-radius: 0.2em;
}

.page-selected {
  display: flex;
  justify-content: center;
  color: black;
  word-break: break-all;
  word-spacing: 0.2em;
  font-weight: bold;
  font-size: 20px;
  padding-top: 10px;
  padding-bottom: 10px;
}

.page-options-btn {
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

.btn:hover {
  background-color: black;
  color: white;
}

.delete-btn {
  cursor: pointer;
}

.enable-btn {
  background-color: wheat;
}
</style>