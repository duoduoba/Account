package com.example.account;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class FlowLayout extends ViewGroup {
	// ��¼ÿ��View��λ��
	private List<ChildPos> mChildPos = new ArrayList<ChildPos>();

	private class ChildPos {
		int left, top, right, bottom;

		public ChildPos(int left, int top, int right, int bottom) {
			this.left = left;
			this.top = top;
			this.right = right;
			this.bottom = bottom;
		}
	}

	public FlowLayout(Context context) {
		this(context, null);
	}

	public FlowLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	/**
	 * ���յ���������췽��
	 * 
	 * @param context
	 *            ������
	 * @param attrs
	 *            xml���Լ���
	 * @param defStyle
	 *            Theme�ж����style
	 */
	public FlowLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * ������Ⱥ͸߶�
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// ��ȡ��ʽ���ֵĿ�Ⱥ�ģʽ
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		// ��ȡ��ʽ���ֵĸ߶Ⱥ�ģʽ
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);

		// ʹ��wrap_content����ʽ���ֵ����տ�Ⱥ͸߶�
		int width = 0, height = 0;
		// ��¼ÿһ�еĿ�Ⱥ͸߶�
		int lineWidth = 0, lineHeight = 0;
		// �õ��ڲ�Ԫ�صĸ���
		int count = getChildCount();
		mChildPos.clear();
		for (int i = 0; i < count; i++) {
			// ��ȡ��Ӧ������view
			View child = getChildAt(i);
			// ������view�Ŀ�͸�
			measureChild(child, widthMeasureSpec, heightMeasureSpec);
			MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
			// ��viewռ�ݵĿ��
			int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
			// ��viewռ�ݵĸ߶�
			int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
			// ����
			if (lineWidth + childWidth > widthSize - getPaddingLeft() - getPaddingRight()) {
				// ȡ�����п�Ϊ��ʽ���ֿ��
				width = Math.max(width, lineWidth);
				// �����иߵõ���ʽ���ָ߶�
				height += lineHeight;
				// �����п��Ϊ��һ��View�Ŀ��
				lineWidth = childWidth;
				// �����и߶�Ϊ��һ��View�ĸ߶�
				lineHeight = childHeight;
				// ��¼λ��
				mChildPos.add(new ChildPos(getPaddingLeft() + lp.leftMargin, getPaddingTop() + height + lp.topMargin,
						getPaddingLeft() + childWidth - lp.rightMargin,
						getPaddingTop() + height + childHeight - lp.bottomMargin));
			} else { // ������
				// ��¼λ��
				mChildPos.add(new ChildPos(getPaddingLeft() + lineWidth + lp.leftMargin,
						getPaddingTop() + height + lp.topMargin,
						getPaddingLeft() + lineWidth + childWidth - lp.rightMargin,
						getPaddingTop() + height + childHeight - lp.bottomMargin));
				// ������View��ȵõ����п��
				lineWidth += childWidth;
				// ȡ��ǰ����View���߶���Ϊ�и߶�
				lineHeight = Math.max(lineHeight, childHeight);
			}
			// ���һ���ؼ�
			if (i == count - 1) {
				width = Math.max(lineWidth, width);
				height += lineHeight;
			}
		}

		// ���ʹ��AT_MOST��wrap_content����ʹ��width��height��Ϊ���
		// ����ʹ��widthSize, heightSize��Ϊ���
		setMeasuredDimension(
				widthMode == MeasureSpec.AT_MOST ? width + getPaddingLeft() + getPaddingRight() : widthSize,
				heightMode == MeasureSpec.AT_MOST ? height + getPaddingTop() + getPaddingBottom() : heightSize);
	}

	/**
	 * ��ViewGroup�ܹ�֧��margin����
	 */
	@Override
	public LayoutParams generateLayoutParams(AttributeSet attrs) {
		return new MarginLayoutParams(getContext(), attrs);
	}

	/**
	 * ����ÿ��View��λ��
	 */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int count = getChildCount();
		for (int i = 0; i < count; i++) {
			View child = getChildAt(i);
			ChildPos pos = mChildPos.get(i);
			// ����View����ߡ��ϱߡ��ұߵױ�λ��
			child.layout(pos.left, pos.top, pos.right, pos.bottom);
		}
	}
}