/**
 * 
 */
package com.xklakoux.freecell;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

/**
 * @author artur
 * 
 */
public class CellPile extends BasePile {

	private static final String TAG = CellPile.class.getSimpleName();

	LayoutInflater inflater;

	public CellPile(Context context, AttributeSet attrs) {
		super(context, attrs);
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

		Game.getStatsManager().updateMoves(StatsManager.MOVE);

		if (Game.getSettings().getBoolean(Constant.SETT_SOUNDS, true)) {
			Game.playSound(Game.SOUND_PUT_CARD);
		}
	}

	@Override
	protected boolean fitOnEachother(Card cardBelow, Card cardOver) {
		BasePile draggedParent = (BasePile) cardOver.getParent();

		if (isEmpty()) {
			if (draggedParent.getCardsCount() == draggedParent.indexOfCard(cardOver) + 1) {
				return true;
			}
		}
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
