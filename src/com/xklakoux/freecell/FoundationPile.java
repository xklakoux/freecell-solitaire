/**
 * 
 */
package com.xklakoux.freecell;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import com.xklakoux.freecell.enums.Number;

/**
 * @author artur
 * 
 */
public class FoundationPile extends BasePile {

	private static final String TAG = FoundationPile.class.getSimpleName();

	LayoutInflater inflater;

	public FoundationPile(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void init() {
		inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.foundation_pile, this, true);
	}

	public boolean uncoverLastCard() {
		if (getCardsCount() > 0) {
			Card lastCard = getLastCard();
			if (!lastCard.isFaceup()) {
				lastCard.setFaceup(true);
				return true;
			}
		}
		return false;
	}

	@Override
	public void addCard(Card movedCard) {

		MarginLayoutParams marginParams = new MarginLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		marginParams.setMargins(0, 0, 0, 0);
		Pile.LayoutParams layoutParams = new Pile.LayoutParams(marginParams);
		movedCard.setLayoutParams(layoutParams);
		addView(movedCard);
	}

	@Override
	protected void moveCards(BasePile draggedParent, Card draggedCard) {
		int indexOfDragged = draggedParent.indexOfCard(draggedCard);
		int movedCards = 0;
		for (int i = indexOfDragged; i < draggedParent.getCardsCount();) {
			draggedParent.getCardAt(i).setVisibility(View.VISIBLE);
			draggedParent.moveCard(this, draggedParent.getCardAt(i));
			movedCards++;
		}

		int indexOfDraggedParent = (Integer) draggedParent.getTag();
		int indexOfLandingParent = (Integer) getTag();

		Game.getMoves().add(
				new Move(movedCards, indexOfDraggedParent, indexOfLandingParent, draggedCard.getSuit(),
						Move.ACTION_MOVE));
		if (checkFullSetAndClear(true)) {
			Game.getLastMove().setCompleted(true);
		}

		Game.getStatsManager().updateMoves(StatsManager.MOVE);

		if (Game.getSettings().getBoolean(Constant.SETT_SOUNDS, true)) {
			Game.playSound(Game.SOUND_PUT_CARD);
		}
	}

	public boolean checkFullSetAndClear(boolean animation) {
		refresh();
		int lastIndex = getCardsCount() - 1;
		int counter = 1;
		Card referenceCard = getLastCard();
		for (int i = lastIndex - 1; i >= 0; i--) {
			Card card = getCardAt(i);
			if (!(fitOnEachother(card, referenceCard))) {
				break;
			}
			referenceCard = card;
			counter++;
		}

		Log.d(TAG, "counter: " + counter);
		if (counter == Game.FULL_NUMBER_SET) {

			Game.setCompleted();
			Game.getStatsManager().updatePoints(StatsManager.SET_COMPLETED);
			return true;
		}
		return false;
	}

	@Override
	protected boolean fitOnEachother(Card cardBelow, Card cardOver) {
		//		BasePile draggedParent = (BasePile) cardOver.getParent();
		// if (draggedParent.getCardsCount() ==
		// draggedParent.indexOfCard(cardOver) + 1) {
		if (cardBelow != null) {

			if (cardBelow.getNumber().getId() + 1 == cardOver.getNumber().getId()
					&& cardBelow.getSuit() == cardOver.getSuit()) {
				return true;
			} else {
				return false;
			}
		} else {
			if (cardOver.getNumber() == Number.ACE) {
				return true;
			}
		}
		// }
		return false;
	}

	@Override
	public void moveCard(BasePile toPile, Card movedCard) {
		removeView(movedCard);
		toPile.addCard(movedCard);
	}

	@Override
	public void removeLastCard() {
		super.removeLastCard();
	}

	@Override
	public void removeCard(Card card) {
		super.removeCard(card);
	}

}
