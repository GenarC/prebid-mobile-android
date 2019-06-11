package org.prebid.mobile.drprebid.ui.viewholders;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import org.prebid.mobile.drprebid.R;
import org.prebid.mobile.drprebid.model.HelpScreen;
import org.prebid.mobile.drprebid.ui.activities.InfoActivity;
import org.prebid.mobile.drprebid.ui.viewmodels.PrebidServerValidationViewModel;
import org.prebid.mobile.drprebid.util.HelpScreenUtil;

import java.util.Locale;

public class PrebidServerValidationViewHolder extends RecyclerView.ViewHolder implements TestResultViewHolder, LifecycleOwner {
    private ProgressBar totalProgress;
    private ImageView totalIcon;

    private TextView sendRequestView;
    private ProgressBar sendRequestProgress;
    private ImageView sendRequestIcon;

    private TextView responseReceivedView;
    private ProgressBar responseReceivedProgress;
    private ImageView responseReceivedIcon;

    private TextView avgCpmView;
    private TextView avgResponseTimeView;

    private boolean sentPassed = false;
    private boolean sentFinished = false;
    private boolean receivedPassed = false;
    private boolean receivedFinished = false;

    public PrebidServerValidationViewHolder(@NonNull final View itemView) {
        super(itemView);

        itemView.findViewById(R.id.button_info).setOnClickListener(v -> {
            HelpScreen aboutScreen = HelpScreenUtil.getRealTimeDemandTestInfo(itemView.getContext());
            Intent intent = InfoActivity.newIntent(itemView.getContext(), aboutScreen.getTitle(), aboutScreen.getHtmlAsset());
            itemView.getContext().startActivity(intent);
        });

        final ProgressBar totalProgress = itemView.findViewById(R.id.progress_total_result);
        final ImageView totalIcon = itemView.findViewById(R.id.image_total_result);

        final TextView sendRequestView = itemView.findViewById(R.id.view_bid_requests_sent_result);
        final ProgressBar sendRequestProgress = itemView.findViewById(R.id.progress_bid_requests_sent_result);
        final ImageView sendRequestIcon = itemView.findViewById(R.id.image_bid_requests_sent_result);

        final TextView responseReceivedView = itemView.findViewById(R.id.view_bid_responses_received_result);
        final ProgressBar responseReceivedProgress = itemView.findViewById(R.id.progress_bid_responses_received_result);
        final ImageView responseReceivedIcon = itemView.findViewById(R.id.image_bid_responses_received_result);

        final TextView avgCpmView = itemView.findViewById(R.id.view_average_cpm);
        final TextView avgResponseTimeView = itemView.findViewById(R.id.view_average_response_time);

        PrebidServerValidationViewModel mViewModel = ViewModelProviders.of((AppCompatActivity) itemView.getContext()).get(PrebidServerValidationViewModel.class);

        mViewModel.getBidRequestsSent().observe(this, sent -> {
            sendRequestProgress.setVisibility(View.GONE);
            sendRequestIcon.setVisibility(View.VISIBLE);

            if (sent) {
                sendRequestIcon.setImageResource(R.drawable.icon_success_step);
                sentPassed = true;
            } else {
                sendRequestIcon.setImageResource(R.drawable.icon_fail_step);
                sentPassed = false;
            }

            sendRequestView.setText(String.format(Locale.ENGLISH, itemView.getContext().getString(R.string.bid_requests_sent), 100));

            sentFinished = true;

            updateTotal();
        });

        mViewModel.getBidResponsesReceived().observe(this, count -> {
            responseReceivedProgress.setVisibility(View.GONE);
            responseReceivedIcon.setVisibility(View.VISIBLE);

            if (count > 0) {
                responseReceivedIcon.setImageResource(R.drawable.icon_success_step);
                receivedPassed = true;
            } else {
                responseReceivedIcon.setImageResource(R.drawable.icon_fail_step);
                receivedPassed = false;
            }

            responseReceivedView.setText(String.format(Locale.ENGLISH, itemView.getContext().getString(R.string.bid_responses_received), count));

            receivedFinished = true;

            updateTotal();
        });

        mViewModel.getAverageResponseTime().observe(this, averageResponseTime -> {
            avgResponseTimeView.setText(String.format(Locale.ENGLISH, itemView.getContext().getString(R.string.average_response_time), averageResponseTime));
        });

        mViewModel.getAverageCpm().observe(this, averageCpm -> {
            avgCpmView.setText(String.format(Locale.ENGLISH, itemView.getContext().getString(R.string.average_cpm), averageCpm));
        });
    }

    @Override
    public void bind() {

    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return ((AppCompatActivity) itemView.getContext()).getLifecycle();
    }

    private void updateTotal() {
        if (sentFinished && receivedFinished) {
            totalProgress.setVisibility(View.GONE);
            totalIcon.setVisibility(View.VISIBLE);
            if (sentPassed && receivedPassed) {
                totalIcon.setImageResource(R.drawable.icon_success_main);
            } else {
                totalIcon.setImageResource(R.drawable.icon_fail_main);
            }
        }
    }
}
