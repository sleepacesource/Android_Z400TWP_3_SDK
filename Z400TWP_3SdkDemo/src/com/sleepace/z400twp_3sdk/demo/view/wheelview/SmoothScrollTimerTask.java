package com.sleepace.z400twp_3sdk.demo.view.wheelview;

import java.util.TimerTask;

final class SmoothScrollTimerTask extends TimerTask {

    int realTotalOffset;
    int realOffset;
    int offset;
    final WheelView loopView;

    SmoothScrollTimerTask(WheelView loopview, int offset) {
        this.loopView = loopview;
        this.offset = offset;
        realTotalOffset = Integer.MAX_VALUE;
        realOffset = 0;
    }

    @Override
    public final void run() {
        if (realTotalOffset == Integer.MAX_VALUE) {
            realTotalOffset = offset;
        }
        //��Ҫ�����ķ�Χϸ�ֳ�ʮС�ݣ�����С�ݵ�λ���ػ�
        realOffset = (int) ((float) realTotalOffset * 0.1F);

        if (realOffset == 0) {
            if (realTotalOffset < 0) {
                realOffset = -1;
            } else {
                realOffset = 1;
            }
        }

        if (Math.abs(realTotalOffset) <= 1) {
            loopView.cancelFuture();
            loopView.handler.sendEmptyMessage(MessageHandler.WHAT_ITEM_SELECTED);
        } else {
            loopView.totalScrollY = loopView.totalScrollY + realOffset;

            //�����������ѭ��ģʽ�������հ�λ����Ҫ�ع�����Ȼ�ͻ����ѡ����1 item�� ���
            if (!loopView.isLoop) {
                float itemHeight = loopView.itemHeight;
                float top = (float) (-loopView.initPosition) * itemHeight;
                float bottom = (float) (loopView.getItemsCount() - 1 - loopView.initPosition) * itemHeight;
                if (loopView.totalScrollY <= top||loopView.totalScrollY >= bottom) {
                    loopView.totalScrollY = loopView.totalScrollY - realOffset;
                    loopView.cancelFuture();
                    loopView.handler.sendEmptyMessage(MessageHandler.WHAT_ITEM_SELECTED);
                    return;
                }
            }
            loopView.handler.sendEmptyMessage(MessageHandler.WHAT_INVALIDATE_LOOP_VIEW);
            realTotalOffset = realTotalOffset - realOffset;
        }
    }
}
