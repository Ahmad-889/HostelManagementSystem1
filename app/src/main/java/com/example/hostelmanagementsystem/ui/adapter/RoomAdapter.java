package com.example.hostelmanagementsystem.ui.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hostelmanagementsystem.R;
import com.example.hostelmanagementsystem.controller.HMSController;
import com.example.hostelmanagementsystem.enums.Facility;
import com.example.hostelmanagementsystem.enums.UserRole;
import com.example.hostelmanagementsystem.model.Room;
import com.example.hostelmanagementsystem.ui.helpers.AddUpdateRoomDialog;
import com.example.hostelmanagementsystem.ui.helpers.SubmitApplicationDialog;

import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {
    private Context context;
    private List<Room> roomList;
    private HMSController controller;
    private UserRole role;
    private String studentId; // only for student

    public RoomAdapter(
            Context context,
            List<Room> roomList,
            HMSController controller,
            UserRole role,
            String studentId
    ) {
        this.context = context;
        this.roomList = roomList;
        this.controller = controller;
        this.role = role;
        this.studentId = studentId;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_room, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {

        Room room = roomList.get(position);

        // ---------- Basic Room Info ----------
        holder.tvRoomNumber.setText("Room " + room.getRoomNumber());
        holder.tvHostelBlock.setText("Block: " + room.getHostelBlock());
        holder.tvFloor.setText("Floor: " + room.getFloor());
        holder.tvRoomType.setText("Type: " + room.getRoomType());
        holder.tvCapacity.setText(
                "Capacity: " + room.getCurrentOccupancy() + "/" + room.getCapacity()
        );
        holder.tvRent.setText(
                "Rent: Rs. " + String.format("%.0f", room.getHostelRent())
        );

        // ---------- Availability ----------
        boolean isRoomAvailable = room.getCurrentOccupancy() < room.getCapacity();
        if (isRoomAvailable) {
            holder.tvAvailability.setText("Available");
            holder.tvAvailability.setTextColor(
                    context.getResources().getColor(android.R.color.holo_green_dark)
            );
            holder.tvAvailability.setBackgroundResource(R.drawable.status_badge_approved);
        } else {
            holder.tvAvailability.setText("Full");
            holder.tvAvailability.setTextColor(
                    context.getResources().getColor(android.R.color.holo_red_dark)
            );
            holder.tvAvailability.setBackgroundResource(R.drawable.status_badge_rejected);
        }

        // ---------- Facilities ----------
        StringBuilder facilities = new StringBuilder("Facilities: ");
        if (room.getFacilities() != null && !room.getFacilities().isEmpty()) {
            for (int i = 0; i < room.getFacilities().size(); i++) {
                facilities.append(formatFacility(room.getFacilities().get(i)));
                if (i < room.getFacilities().size() - 1) {
                    facilities.append(", ");
                }
            }
        } else {
            facilities.append("None");
        }
        holder.tvFacilities.setText(facilities.toString());

        // ==================================================
        // ROLE-BASED BUTTON LOGIC
        // ==================================================

        if (role == UserRole.ADMIN) {

            // ---- ADMIN VIEW ----
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnApply.setVisibility(View.GONE);

            holder.btnEdit.setOnClickListener(v -> {
                AddUpdateRoomDialog dialog =
                        new AddUpdateRoomDialog(context, controller, room);
                dialog.show();
            });

            holder.btnDelete.setOnClickListener(v -> {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    showDeleteConfirmation(room, adapterPosition);
                }
            });

        } else {

            // ---- STUDENT VIEW ----
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
            holder.btnApply.setVisibility(View.VISIBLE);

            // Disable apply if room is full
            holder.btnApply.setEnabled(room.isAvailable());

            holder.btnApply.setOnClickListener(v -> {
                SubmitApplicationDialog dialog = new SubmitApplicationDialog(
                        context,
                        controller,
                        room,
                        studentId,
                        () -> {
                            // Callback when application is submitted
                            Toast.makeText(context, "Application submitted successfully",
                                    Toast.LENGTH_SHORT).show();
                        }
                );
                dialog.show();
            });
        }
    }


    @Override
    public int getItemCount() {
        return roomList.size();
    }

    private String formatFacility(Facility facility) {
        switch (facility) {
            case AC:
                return "AC";
            case WIFI:
                return "WiFi";
            case ATTACHED_BATH:
                return "Attached Bath";
            case HEATER:
                return "Heater";
            case STUDY_TABLE:
                return "Study Table";
            default:
                return facility.toString();
        }
    }

    private void showDeleteConfirmation(Room room, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Delete Room")
                .setMessage("Are you sure you want to delete Room " + room.getRoomNumber() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    controller.removeRoom(room.getRoomId(), success -> {
                        if (success) {
                            roomList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, roomList.size());
                            Toast.makeText(context, "Room deleted successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Failed to delete room", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    static class RoomViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoomNumber, tvHostelBlock, tvFloor, tvRoomType, tvCapacity, tvRent, tvFacilities, tvAvailability;
        Button btnEdit, btnDelete, btnApply;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRoomNumber = itemView.findViewById(R.id.tvRoomNumber);
            tvHostelBlock = itemView.findViewById(R.id.tvHostelBlock);
            tvFloor = itemView.findViewById(R.id.tvFloor);
            tvRoomType = itemView.findViewById(R.id.tvRoomType);
            tvCapacity = itemView.findViewById(R.id.tvCapacity);
            tvRent = itemView.findViewById(R.id.tvRent);
            tvFacilities = itemView.findViewById(R.id.tvFacilities);
            tvAvailability = itemView.findViewById(R.id.tvAvailability);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnApply = itemView.findViewById(R.id.btnApply);
        }
    }
}