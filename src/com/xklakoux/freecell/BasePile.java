/**
 * 
 */
package com.xklakoux.freecell;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnDragListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * @author artur
 * 
 */
public abstract class BasePile extends RelativeLayout implements OnDragListener{

	private static final String TAG = BasePile.class.getSimpleName();

	LayoutInflater inflater;

	public BasePile(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
		setOnDragListener(this);
	}

	public Card getCardAt(int index) {
		return (Card) super.getChildAt(index + 1);
	}

	public int getCardsCount() {
		return super.getChildCount() - 1;
	}

	public int indexOfCard(Card child) {
		return super.indexOfChild(child) - 1;
	}

	public Card getLastCard() {
		if (getCardsCount() > 0) {
			return (Card) super.getChildAt(super.getChildCount() - 1);
		}
		return null;
	}

	public boolean isEmpty() {
		if (getCardsCount() > 0) {
			return false;
		}
		return true;
	}

	public ImageView getLastTrueChild() {
		return (ImageView) super.getChildAt(super.getChildCount() - 1);
	}

	public ImageView getFirstCardSpot() {
		return (ImageView) super.getChildAt(0);
	}

	public void init() {
		inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.pile, this, true);
	}

	public void removeLastCard() {
		super.removeViewAt(super.getChildCount() - 1);
	}

	public void addCard(Card movedCard) {
		addView(movedCard);
	}

	public void moveCard(BasePile toPile, Card movedCard) {
		removeView(movedCard);
		toPile.addCard(movedCard);

	}

	public void refresh() {
		for (int i = 0; i < getCardsCount(); i++) {
			Card c = getCardAt(i);
			c.setFaceup(c.isFaceup());
			c.setColorFilter(Color.TRANSPARENT);
		}
	}

	public void setHighlight(boolean highlight) {
		if (highlight) {
			getLastTrueChild().setColorFilter(getResources().getColor(R.color.highlight));
		} else {
			getLastTrueChild().setColorFilter(getResources().getColor(android.R.color.transparent));
		}
	}

	abstract protected boolean fitOnEachother(Card cardBelow, Card cardOver);

	abstract protected void moveCards(BasePile draggedParent, Card draggedCard);

	protected boolean onCardsDrag(View v, DragEvent event) {
		Card draggedCard = (Card) event.getLocalState();
		if (draggedCard==null || draggedCard.getParent() == null || this==draggedCard.getParent()) {
			return false;
		}
		BasePile draggedParent = (BasePile) draggedCard.getParent();

		switch (event.getAction()) {
		case DragEvent.ACTION_DRAG_STARTED:
			break;
		case DragEvent.ACTION_DRAG_ENTERED:
			setHighlight(true);
			break;
		case DragEvent.ACTION_DRAG_EXITED:

			setHighlight(false);
			break;
		case DragEvent.ACTION_DROP:
			setHighlight(false);

			if(!fitOnEachother(getLastCard(), draggedCard)){
				break;
			}
			moveCards(draggedParent,draggedCard);
			return true;
		case DragEvent.ACTION_DRAG_ENDED:
			setHighlight(false);
			int indexOfDragged = draggedParent.indexOfCard(draggedCard);
			for (int i = indexOfDragged; i < draggedParent.getCardsCount(); i++) {
				Card card = draggedParent.getCardAt(i);
				card.setVisibility(View.VISIBLE);
			}
			return false;
		default:
			break;
		}
		return true;
	}
	@Override
	public boolean onDrag(View v, DragEvent event) {
		return onCardsDrag(v, event);
	}

	public void removeCard(Card card) {
		super.removeView(card);
	}


}
