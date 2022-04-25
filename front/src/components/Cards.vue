<template>
  <div class="cards">
    <div class="card-order">
      <prompt :prompt="$filters.localize('prompt-card-order')"
        >&#128269;</prompt
      >
      {{ $filters.localize("cardes.merge-order") }}
    </div>

    <div class="cards-board">
      <div
        v-for="card in cards"
        :key="card"
        class="card"
        :class="{
          'card-drag': card.drag,
          'card-drop': card.drop,
          'card-static': !card.drop,
        }"
      >
        <div class="card-number">
          <span style="word-wrap: break-word">
            {{card.filename}}
            </span
          >
        </div>
        <div class="card-delete">
          <button
            @click="deletePage(card)"
            class="delete-btn"
            type="button"
            title="DELETE"
          >
            &#10060;
          </button>
        </div>
        <div>
          <img
            class="card-img"
            draggable="true"
            @dragstart="dragStart($event, card)"
            @dragend="dragEnd(card)"
            @dragenter="dragEnter(card)"
            @dragleave="dragLeave(card)"
            @drop="onDrop($event, card)"
            @dragover.prevent
            @dragenter.prevent
            @contextmenu.prevent="card.full = true"
            :src="card.imgLink"
            :alt="card.imgLink"
          />
        </div>
        <modal-image
          v-if="card.full"
          :image="card.imgLink"
          @close="card.full = false"
        ></modal-image>
      </div>
    </div>
  </div>
</template>

<script>
import Prompt from "./Prompt";
import ModalImage from "./ModalImage.vue";

export default {
  name: "Cards",
  components: { Prompt, ModalImage },
  emits: ["order", "close"],
  data() {
    return {
      isDeleteEnable: false,
      cards: this.images,
      initialCards: [],
      order: [],
      full: false,
    };
  },
  props: {
    images: Array,
  },
  watch: {
    images: {
      immediate: true,
      handler(cur, old) {
        this.order = [];
        if (old === undefined) {
          this.initialCards = this.cards.slice();
          for (let i = 0; i < this.cards.length; i++) {
            this.order.push(this.cards[i].filename);
            this.$emit("order", this.order);
          }
          return;
        }
        if (old !== cur) {
          this.cards = cur;
        }
        if (old === cur) {
          this.cards = cur;
        }
        for (let i = 0; i < this.cards.length; i++) {
          this.order.push(this.cards[i].filename);
          this.$emit("order", this.order);
        }
        this.initialCards = this.cards.slice();
      },
    },
  },
  methods: {
    dragEnd(card) {
      const index = this.cards.indexOf(card);
      this.cards[index].drag = false;
      this.$emit("order", this.order);
    },
    dragEnter(card) {
      const index = this.cards.indexOf(card);
      this.cards[index].drop = true;
    },
    dragLeave(card) {
      const index = this.cards.indexOf(card);
      this.cards[index].drop = false;
    },
    dragStart(evt, card) {
      const index = this.cards.indexOf(card);
      this.cards[index].drag = true;
      evt.dataTransfer.dropEffect = "move";
      evt.dataTransfer.effectAllowed = "move";
      evt.dataTransfer.setData("cardNum", card.filename);
    },
    onDrop(evt, currentCard) {
      const dragCardNumber = evt.dataTransfer.getData("cardNum");
      const dragCard = this.cards.find(
        (card) => card.filename === dragCardNumber
      );
      const dragCardIndex = this.cards.indexOf(dragCard);
      const currentCardIndex = this.cards.indexOf(currentCard);
      //swap
      setTimeout(() => {
        this.cards[dragCardIndex] = currentCard;
        this.cards[currentCardIndex] = dragCard;
      }, 300);
      //disable all
      this.cards[currentCardIndex].drop = false;
      this.cards[dragCardIndex].drop = false;
      this.cards[currentCardIndex].drag = false;
      this.cards[dragCardIndex].drag = false;
      //swap checkbox
      const dragCardNumberIndex = this.order.indexOf(dragCard.filename);
      const currentCardNumberIndex = this.order.indexOf(currentCard.filename);
      if (dragCardNumberIndex !== -1 && currentCardNumberIndex !== -1) {
        this.order[dragCardNumberIndex] = currentCard.filename;
        this.order[currentCardNumberIndex] = dragCard.filename;
      }
    },
    deletePage(card) {
      const orderArrIndex = this.order.indexOf(card.filename);
      this.order.splice(orderArrIndex, 1);
      const cardArrIndex = this.cards.indexOf(card);
      this.cards.splice(cardArrIndex, 1);
      if (this.cards.length === 0) {
        this.cards = this.initialCards;
        this.initialCards = this.initialCards.slice();
        for (let i = 0; i < this.cards.length; i++) {
          this.order.push(this.cards[i].filename);
        }
      }
    },
  },
};
</script>

<style scoped>
.cards {
  display: flex;
  flex-direction: column;
  align-content: center;
  margin: 0 5em 0 5em;
  width: 100%;
}

.cards-board {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  background-color: lightgray;
  border-radius: 0.2em;
}

.card {
  width: 170px;
  height: 250px;
  position: relative;
  margin-left: 10px;
  margin-right: 10px;
  margin-bottom: 5px;
}

.card-static {
  border: 2px solid black;
  border-radius: 4px;
}

.card-drag {
  border: 2px dashed blue;
  border-radius: 4px;
  opacity: 0.6;
}

.card-drop {
  transition: 0.25s;
  border: 2px inset blue;
  border-radius: 4px;
  -moz-box-shadow: 0 0 10px blue;
  -webkit-box-shadow: 0 0 10px blue;
  box-shadow: 0 0 10px blue;
}

.card:hover .card-number {
  background-color: wheat;
  font-size: 12px;
  font-weight: bold;
  transition: 500ms;
}

.card-number {
  position: absolute;
  left: 0;
  bottom: 0;
  font-size: 8pt;
  max-width: 168px;
  max-height: 50px;
  background-color: lightgray;
  color: black;
  text-align: center;
  font-weight: bold;
  border: 1px solid black;
  border-radius: 1px;
  transition: 500ms;
}

.card-img {
  width: 170px;
  height: 250px;
  border-radius: 2px;
}

.card-delete {
  position: absolute;
  right: 0;
  top: 0;
}

.card-order {
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

.delete-btn {
  cursor: pointer;
}
</style>