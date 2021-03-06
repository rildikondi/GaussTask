package com.akondi.interfaceadapters.presenters.gausssolve;

import com.akondi.interfaceadapters.viewmodels.GaussSolveViewModel;
import com.akondi.usecases.gausssolve.GaussSolveOutputBoundary;
import com.akondi.usecases.gausssolve.GaussSolveResponse;

public class GaussSolvePresenter implements GaussSolveOutputBoundary, GaussSolvePresenterOutputBoundary {
    private GaussSolveViewModel viewModel;

    @Override
    public GaussSolveViewModel getViewModel() {
        return viewModel;
    }

    @Override
    public void present(GaussSolveResponse responseModel) {
        viewModel = new GaussSolveViewModel(responseModel.getSolveResponse());
    }
}
