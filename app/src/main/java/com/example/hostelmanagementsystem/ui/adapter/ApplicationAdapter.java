package com.example.hostelmanagementsystem.ui.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hostelmanagementsystem.R;
import com.example.hostelmanagementsystem.controller.HMSController;
import com.example.hostelmanagementsystem.enums.ApplicationStatus;
import com.example.hostelmanagementsystem.model.HostelApplication;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.ApplicationViewHolder> {
    private Context context;
    private List<HostelApplication> applicationList;
    private HMSController controller;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public ApplicationAdapter(Context context, List<HostelApplication> applicationList, HMSController controller) {
        this.context = context;
        this.applicationList = applicationList;
        this.controller = controller;
    }

    @NonNull
    @Override
    public ApplicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_application, parent, false);
        return new ApplicationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApplicationViewHolder holder, int position) {
        HostelApplication app = applicationList.get(position);

        // Load student info - CHECK FOR NULL FIRST
        if (app.getStudentId() != null && !app.getStudentId().isEmpty()) {
            controller.getStudentById(app.getStudentId(), student -> {
                if (student != null) {
                    holder.tvStudentName.setText(student.getName());
                    holder.tvStudentId.setText("ID: " + student.getRegistrationNo());
                } else {
                    holder.tvStudentName.setText("Student ID: " + app.getStudentId());
                    holder.tvStudentId.setText("");
                }
            });
        } else {
            holder.tvStudentName.setText("Unknown Student");
            holder.tvStudentId.setText("ID: N/A");
        }

        // Load room info - CHECK FOR NULL FIRST
        if (app.getRequestedRoomId() != null && !app.getRequestedRoomId().isEmpty()) {
            controller.getRoomById(app.getRequestedRoomId(), room -> {
                if (room != null) {
                    holder.tvRoomInfo.setText("Room: " + room.getRoomNumber() + "-" + room.getHostelBlock());
                } else {
                    holder.tvRoomInfo.setText("Room: N/A");
                }
            });
        } else {
            holder.tvRoomInfo.setText("Room: N/A");
        }

        // Application details
        holder.tvSemester.setText("Semester: " + app.getSemester());
        holder.tvCgpa.setText("CGPA: " + String.format("%.2f", app.getCgpa()));
        holder.tvParentContact.setText("Parent: " + app.getParentContact());
        holder.tvEmergencyContact.setText("Emergency: " + app.getEmergencyContact());

        if (app.getSubmissionDate() != null) {
            holder.tvSubmissionDate.setText("Date: " + dateFormat.format(app.getSubmissionDate()));
        }

        // Status handling
        ApplicationStatus status = app.getStatus();
        holder.tvStatus.setText(status.toString());

        switch (status) {
            case PENDING:
                holder.tvStatus.setTextColor(context.getResources().getColor(android.R.color.holo_orange_dark));
                holder.tvStatus.setBackgroundResource(R.drawable.status_badge_pending);
                holder.layoutActions.setVisibility(View.VISIBLE);
                holder.tvRejectionReason.setVisibility(View.GONE);
                break;

            case APPROVED:
                holder.tvStatus.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
                holder.tvStatus.setBackgroundResource(R.drawable.status_badge_approved);
                holder.layoutActions.setVisibility(View.GONE);
                holder.tvRejectionReason.setVisibility(View.GONE);
                break;

            case REJECTED:
                holder.tvStatus.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
                holder.tvStatus.setBackgroundResource(R.drawable.status_badge_rejected);
                holder.layoutActions.setVisibility(View.GONE);
                if (app.getRejectionReason() != null && !app.getRejectionReason().isEmpty()) {
                    holder.tvRejectionReason.setText("Reason: " + app.getRejectionReason());
                    holder.tvRejectionReason.setVisibility(View.VISIBLE);
                }
                break;
        }

        // Approve button
        holder.btnApprove.setOnClickListener(v -> {
            showApproveConfirmation(app, position);
        });

        // Reject button
        holder.btnReject.setOnClickListener(v -> {
            showRejectDialog(app, position);
        });
    }

    @Override
    public int getItemCount() {
        return applicationList.size();
    }

    private void showApproveConfirmation(HostelApplication app, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Approve Application")
                .setMessage("Are you sure you want to approve this application? The student will be allocated to the room.")
                .setPositiveButton("Approve", (dialog, which) -> {
                    controller.approveApplication(context, app, success -> {
                        if (success) {
                            app.setStatus(ApplicationStatus.APPROVED);
                            notifyItemChanged(position);
                            Toast.makeText(context, "Application approved successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Failed to approve application", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showRejectDialog(HostelApplication app, int position) {
        // Create custom dialog with EditText for reason
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Reject Application");
        builder.setMessage("Please provide a reason for rejection:");

        final EditText input = new EditText(context);
        input.setHint("Enter rejection reason");
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(50, 20, 50, 20);
        input.setLayoutParams(params);

        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        container.addView(input);
        builder.setView(container);

        builder.setPositiveButton("Reject", (dialog, which) -> {
            String reason = input.getText().toString().trim();
            if (reason.isEmpty()) {
                Toast.makeText(context, "Please provide a reason", Toast.LENGTH_SHORT).show();
                return;
            }

            controller.rejectApplication(context, app, reason, success -> {
                if (success) {
                    app.setStatus(ApplicationStatus.REJECTED);
                    app.setRejectionReason(reason);
                    notifyItemChanged(position);
                    Toast.makeText(context, "Application rejected", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Failed to reject application", Toast.LENGTH_SHORT).show();
                }
            });
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    static class ApplicationViewHolder extends RecyclerView.ViewHolder {
        TextView tvStudentName, tvStudentId, tvStatus, tvSemester, tvCgpa, tvRoomInfo;
        TextView tvSubmissionDate, tvParentContact, tvEmergencyContact, tvRejectionReason;
        LinearLayout layoutActions;
        Button btnApprove, btnReject;

        public ApplicationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            tvStudentId = itemView.findViewById(R.id.tvStudentId);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvSemester = itemView.findViewById(R.id.tvSemester);
            tvCgpa = itemView.findViewById(R.id.tvCgpa);
            tvRoomInfo = itemView.findViewById(R.id.tvRoomInfo);
            tvSubmissionDate = itemView.findViewById(R.id.tvSubmissionDate);
            tvParentContact = itemView.findViewById(R.id.tvParentContact);
            tvEmergencyContact = itemView.findViewById(R.id.tvEmergencyContact);
            tvRejectionReason = itemView.findViewById(R.id.tvRejectionReason);
            layoutActions = itemView.findViewById(R.id.layoutActions);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}