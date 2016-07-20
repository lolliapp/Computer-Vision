// Generated code from Butter Knife. Do not modify!
package com.lolliapp.opencvtemplate;

import android.view.View;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Finder;
import butterknife.internal.ViewBinder;
import java.lang.IllegalStateException;
import java.lang.Object;
import java.lang.Override;

public class MainActivity$$ViewBinder<T extends MainActivity> implements ViewBinder<T> {
  @Override
  public Unbinder bind(final Finder finder, final T target, Object source) {
    InnerUnbinder unbinder = createUnbinder(target);
    View view;
    view = finder.findRequiredView(source, 2131558500, "field 'aboutContainer' and method 'onAboutContainerClick'");
    target.aboutContainer = finder.castView(view, 2131558500, "field 'aboutContainer'");
    unbinder.view2131558500 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onAboutContainerClick();
      }
    });
    view = finder.findRequiredView(source, 2131558501, "field 'licenseTextView'");
    target.licenseTextView = finder.castView(view, 2131558501, "field 'licenseTextView'");
    view = finder.findRequiredView(source, 2131558486, "field 'mOpenCvCameraView'");
    target.mOpenCvCameraView = finder.castView(view, 2131558486, "field 'mOpenCvCameraView'");
    view = finder.findRequiredView(source, 2131558488, "field 'controlsContainer'");
    target.controlsContainer = finder.castView(view, 2131558488, "field 'controlsContainer'");
    view = finder.findRequiredView(source, 2131558490, "field 'edgeDetectorMode' and method 'onEdgeDetectorModeClick'");
    target.edgeDetectorMode = finder.castView(view, 2131558490, "field 'edgeDetectorMode'");
    unbinder.view2131558490 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onEdgeDetectorModeClick();
      }
    });
    view = finder.findRequiredView(source, 2131558491, "field 'faceDetectorMode' and method 'onFaceDetectorModeCick'");
    target.faceDetectorMode = finder.castView(view, 2131558491, "field 'faceDetectorMode'");
    unbinder.view2131558491 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onFaceDetectorModeCick();
      }
    });
    view = finder.findRequiredView(source, 2131558493, "field 'rotationDegrees'");
    target.rotationDegrees = finder.castView(view, 2131558493, "field 'rotationDegrees'");
    view = finder.findRequiredView(source, 2131558496, "field 'cannyValueContainer'");
    target.cannyValueContainer = finder.castView(view, 2131558496, "field 'cannyValueContainer'");
    view = finder.findRequiredView(source, 2131558497, "field 'cannyThreshold'");
    target.cannyThreshold = finder.castView(view, 2131558497, "field 'cannyThreshold'");
    view = finder.findRequiredView(source, 2131558487, "method 'onSwitchCameraBtnClick'");
    unbinder.view2131558487 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onSwitchCameraBtnClick();
      }
    });
    view = finder.findRequiredView(source, 2131558499, "method 'onDecreaseCannyThresholdClick'");
    unbinder.view2131558499 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onDecreaseCannyThresholdClick();
      }
    });
    view = finder.findRequiredView(source, 2131558498, "method 'onIncreaseCannyThresholdClick'");
    unbinder.view2131558498 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onIncreaseCannyThresholdClick();
      }
    });
    view = finder.findRequiredView(source, 2131558494, "method 'onRotateCCWClck'");
    unbinder.view2131558494 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onRotateCCWClck();
      }
    });
    view = finder.findRequiredView(source, 2131558495, "method 'onRotateCWClick'");
    unbinder.view2131558495 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onRotateCWClick();
      }
    });
    view = finder.findRequiredView(source, 2131558484, "method 'onAboutBtnClick'");
    unbinder.view2131558484 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onAboutBtnClick();
      }
    });
    return unbinder;
  }

  protected InnerUnbinder<T> createUnbinder(T target) {
    return new InnerUnbinder(target);
  }

  protected static class InnerUnbinder<T extends MainActivity> implements Unbinder {
    private T target;

    View view2131558500;

    View view2131558490;

    View view2131558491;

    View view2131558487;

    View view2131558499;

    View view2131558498;

    View view2131558494;

    View view2131558495;

    View view2131558484;

    protected InnerUnbinder(T target) {
      this.target = target;
    }

    @Override
    public final void unbind() {
      if (target == null) throw new IllegalStateException("Bindings already cleared.");
      unbind(target);
      target = null;
    }

    protected void unbind(T target) {
      view2131558500.setOnClickListener(null);
      target.aboutContainer = null;
      target.licenseTextView = null;
      target.mOpenCvCameraView = null;
      target.controlsContainer = null;
      view2131558490.setOnClickListener(null);
      target.edgeDetectorMode = null;
      view2131558491.setOnClickListener(null);
      target.faceDetectorMode = null;
      target.rotationDegrees = null;
      target.cannyValueContainer = null;
      target.cannyThreshold = null;
      view2131558487.setOnClickListener(null);
      view2131558499.setOnClickListener(null);
      view2131558498.setOnClickListener(null);
      view2131558494.setOnClickListener(null);
      view2131558495.setOnClickListener(null);
      view2131558484.setOnClickListener(null);
    }
  }
}
