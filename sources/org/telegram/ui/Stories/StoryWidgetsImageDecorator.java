package org.telegram.ui.Stories;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageReceiver;
import org.telegram.tgnet.TLRPC$StoryItem;
import org.telegram.tgnet.TLRPC$TL_mediaAreaCoordinates;
import org.telegram.tgnet.TLRPC$TL_mediaAreaSuggestedReaction;
import org.telegram.ui.Components.Reactions.ReactionImageHolder;
import org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble;
public class StoryWidgetsImageDecorator extends ImageReceiver.Decorator {
    ArrayList<DrawingObject> drawingObjects;
    float imageH;
    float imageW;
    float imageX;
    float imageY;

    public static abstract class DrawingObject {
        public abstract void draw(Canvas canvas, ImageReceiver imageReceiver, float f);

        public abstract void onAttachedToWindow(boolean z);

        public abstract void setParent(View view);
    }

    public StoryWidgetsImageDecorator(TLRPC$StoryItem tLRPC$StoryItem) {
        for (int i = 0; i < tLRPC$StoryItem.media_areas.size(); i++) {
            if (tLRPC$StoryItem.media_areas.get(i) instanceof TLRPC$TL_mediaAreaSuggestedReaction) {
                if (this.drawingObjects == null) {
                    this.drawingObjects = new ArrayList<>();
                }
                this.drawingObjects.add(new ReactionWidget((TLRPC$TL_mediaAreaSuggestedReaction) tLRPC$StoryItem.media_areas.get(i)));
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas, ImageReceiver imageReceiver) {
        if (this.drawingObjects == null) {
            return;
        }
        float alpha = imageReceiver.getAlpha();
        float centerX = imageReceiver.getCenterX();
        float centerY = imageReceiver.getCenterY();
        float imageWidth = imageReceiver.getImageWidth();
        this.imageW = imageWidth;
        float f = (16.0f * imageWidth) / 9.0f;
        this.imageH = f;
        this.imageX = centerX - (imageWidth / 2.0f);
        this.imageY = centerY - (f / 2.0f);
        for (int i = 0; i < this.drawingObjects.size(); i++) {
            this.drawingObjects.get(i).draw(canvas, imageReceiver, alpha);
        }
    }

    @Override
    public void onAttachedToWindow(ImageReceiver imageReceiver) {
        if (this.drawingObjects == null) {
            return;
        }
        for (int i = 0; i < this.drawingObjects.size(); i++) {
            this.drawingObjects.get(i).setParent(imageReceiver.getParentView());
            this.drawingObjects.get(i).onAttachedToWindow(true);
        }
    }

    @Override
    public void onDetachedFromWidnow() {
        if (this.drawingObjects == null) {
            return;
        }
        for (int i = 0; i < this.drawingObjects.size(); i++) {
            this.drawingObjects.get(i).onAttachedToWindow(false);
        }
    }

    public class ReactionWidget extends DrawingObject {
        TLRPC$TL_mediaAreaSuggestedReaction mediaArea;
        StoryReactionWidgetBackground storyReactionWidgetBackground = new StoryReactionWidgetBackground(null);
        ReactionImageHolder imageHolder = new ReactionImageHolder(null);

        public ReactionWidget(TLRPC$TL_mediaAreaSuggestedReaction tLRPC$TL_mediaAreaSuggestedReaction) {
            this.mediaArea = tLRPC$TL_mediaAreaSuggestedReaction;
            if (tLRPC$TL_mediaAreaSuggestedReaction.flipped) {
                this.storyReactionWidgetBackground.setMirror(true, false);
            }
            if (tLRPC$TL_mediaAreaSuggestedReaction.dark) {
                this.storyReactionWidgetBackground.nextStyle();
            }
            this.imageHolder.setStatic();
            this.imageHolder.setVisibleReaction(ReactionsLayoutInBubble.VisibleReaction.fromTLReaction(tLRPC$TL_mediaAreaSuggestedReaction.reaction));
        }

        @Override
        public void draw(Canvas canvas, ImageReceiver imageReceiver, float f) {
            StoryWidgetsImageDecorator storyWidgetsImageDecorator = StoryWidgetsImageDecorator.this;
            double d = storyWidgetsImageDecorator.imageX;
            float f2 = storyWidgetsImageDecorator.imageW;
            double d2 = f2;
            TLRPC$TL_mediaAreaCoordinates tLRPC$TL_mediaAreaCoordinates = this.mediaArea.coordinates;
            double d3 = tLRPC$TL_mediaAreaCoordinates.x;
            Double.isNaN(d2);
            Double.isNaN(d);
            float f3 = (float) (d + ((d2 * d3) / 100.0d));
            double d4 = storyWidgetsImageDecorator.imageY;
            float f4 = storyWidgetsImageDecorator.imageH;
            double d5 = f4;
            double d6 = tLRPC$TL_mediaAreaCoordinates.y;
            Double.isNaN(d5);
            Double.isNaN(d4);
            float f5 = (float) (d4 + ((d5 * d6) / 100.0d));
            double d7 = f2;
            double d8 = tLRPC$TL_mediaAreaCoordinates.w;
            Double.isNaN(d7);
            double d9 = f4;
            double d10 = tLRPC$TL_mediaAreaCoordinates.h;
            Double.isNaN(d9);
            float f6 = ((float) ((d7 * d8) / 100.0d)) / 2.0f;
            float f7 = ((float) ((d9 * d10) / 100.0d)) / 2.0f;
            this.storyReactionWidgetBackground.setBounds((int) (f3 - f6), (int) (f5 - f7), (int) (f6 + f3), (int) (f7 + f5));
            this.storyReactionWidgetBackground.setAlpha((int) (255.0f * f));
            canvas.save();
            double d11 = this.mediaArea.coordinates.rotation;
            if (d11 != 0.0d) {
                canvas.rotate((float) d11, f3, f5);
            }
            Rect rect = AndroidUtilities.rectTmp2;
            float height = (this.storyReactionWidgetBackground.getBounds().height() * 0.61f) / 2.0f;
            rect.set((int) (this.storyReactionWidgetBackground.getBounds().centerX() - height), (int) (this.storyReactionWidgetBackground.getBounds().centerY() - height), (int) (this.storyReactionWidgetBackground.getBounds().centerX() + height), (int) (this.storyReactionWidgetBackground.getBounds().centerY() + height));
            this.storyReactionWidgetBackground.updateShadowLayer(1.0f);
            this.storyReactionWidgetBackground.draw(canvas);
            this.imageHolder.setBounds(rect);
            this.imageHolder.setAlpha(f);
            this.imageHolder.setColor(this.storyReactionWidgetBackground.isDarkStyle() ? -1 : -16777216);
            this.imageHolder.draw(canvas);
            canvas.restore();
        }

        @Override
        public void onAttachedToWindow(boolean z) {
            this.imageHolder.onAttachedToWindow(z);
        }

        @Override
        public void setParent(View view) {
            this.imageHolder.setParent(view);
        }
    }
}
