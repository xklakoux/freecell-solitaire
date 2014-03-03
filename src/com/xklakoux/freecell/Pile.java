/**
 * 
 */
package com.xklakoux.freecell;

import com.xklakoux.freecell.enums.Suit;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

/**
 * @author artur
 * 
 */
public class Pile extends BasePile {

	private static final String TAG = Pile.class.getSimpleName();

	LayoutInflater inflater;

	public Pile(Context context, AttributeSet attrs) {
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

		int marginTop = calculateMarginTop(this);

		MarginLayoutParams marginParams = new MarginLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		marginParams.setMargins(0, marginTop, 0, 0);
		Pile.LayoutParams layoutParams = new Pile.LayoutParams(marginParams);
		movedCard.setLayoutParams(layoutParams);
		addView(movedCard);
		checkSorted(false);
	}

	private int calculateMarginTop(Pile pile) {
		int marginTop = 0;
		Context context = Game.getAppContext();
		for (int i = 0; i < pile.getCardsCount(); i++) {
			Card card = pile.getCardAt(i);
			float tempMargin = card.isFaceup() ? context.getResources().getDimension(R.dimen.card_stack_margin_up)
					: context.getResources().getDimension(R.dimen.card_stack_margin_down);
			marginTop += tempMargin;
		}
		return marginTop;
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
		if (checkSorted(false)) {
			Game.getLastMove().setCompleted(true);
		}

		Game.getStatsManager().updateMoves(StatsManager.MOVE);

		if (Game.getSettings().getBoolean(Constant.SETT_SOUNDS, true)) {
			Game.playSound(Game.SOUND_PUT_CARD);
		}
	}

	public boolean checkSorted(boolean animation) {
		refresh();
		int lastIndex = getCardsCount() - 1;
		Card referenceCard = getLastCard();
		int i;
		for (i = lastIndex - 1; i >= 0; i--) {
			Card card = getCardAt(i);
			if (!fitOnEachother(card, referenceCard)) {
				if (Game.getSettings().getBoolean(Constant.SETT_HINTS, true)) {
					for (int j = i; j >= 0; j--) {
						Card c = getCardAt(j);
						if (c.isFaceup()) {
							c.setColorFilter(getResources().getColor(R.color.dim));
						} else {
							break;
						}
					}
				}
				break;
			}
			referenceCard = card;
		}
		if(i==0) {
			return true;
		}
		return false;
	}

	@Override
	protected boolean fitOnEachother(Card cardBelow, Card cardOver) {
		if (cardBelow != null) {
			if (((cardBelow.getSuit() == Suit.CLUBS || cardBelow.getSuit() == Suit.SPADES) && (cardOver.getSuit() == Suit.HEARTS || cardOver
					.getSuit() == Suit.DIAMONDS))
					|| ((cardBelow.getSuit() == Suit.HEARTS || cardBelow.getSuit() == Suit.DIAMONDS) && (cardOver
							.getSuit() == Suit.SPADES || cardOver.getSuit() == Suit.CLUBS))) {
				if (cardBelow.getNumber().getId() == cardOver.getNumber().getId() + 1) {
					return true;
				}
			}
			return false;
		}
		return true;

	}

	@Override
	public void moveCard(BasePile toPile, Card movedCard) {
		removeView(movedCard);
		toPile.addCard(movedCard);
	}

	@Override
	public void removeLastCard() {
		super.removeLastCard();
		checkSorted(false);
	}

	@Override
	public void removeCard(Card card) {
		super.removeCard(card);
		checkSorted(false);
	}

}
