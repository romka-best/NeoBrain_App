package com.example.neobrain.Adapters;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bluelinelabs.conductor.Router;
import com.example.neobrain.API.model.User;
import com.example.neobrain.R;
import com.example.neobrain.utils.BaseViewHolder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.neobrain.MainActivity.MY_SETTINGS;

public class ProfileInfoAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final String TAG = "ProfileInfoAdapter";
    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_DETAILED_INFO = 1;
    private static final int VIEW_TYPE_EDIT_INFO = 2;
    private static final int VIEW_TYPE_HELPER_TEXT = 3;

    private User mUser;
    private User infoUser;
    private String[] titles;
    private SharedPreferences sp;
    private Router mRouter;
    private boolean isEdit;

    public ProfileInfoAdapter(User user, Router router, boolean isEdit) {
        mUser = user;
        mRouter = router;
        sp = Objects.requireNonNull(router.getActivity()).getSharedPreferences(MY_SETTINGS,
                Context.MODE_PRIVATE);
        this.isEdit = isEdit;
        this.infoUser = new User();
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_DETAILED_INFO:
                return new DetailedViewHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.recycler_view_item_profile_info, parent, false));
            case VIEW_TYPE_EDIT_INFO:
                return new EditViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycler_view_item_profile_edit, parent, false));
            case VIEW_TYPE_HELPER_TEXT:
                return new HelperTextViewHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.recycler_view_item_helper_message, parent, false));
            case VIEW_TYPE_EMPTY:
            default:
                return new EmptyViewHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.recycler_view_empty_item_profile_info, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    public int getItemViewType(int position) {
        if (mUser != null && !isEdit && isNormalInfo()) {
            return VIEW_TYPE_DETAILED_INFO;
        } else if (mUser != null && isEdit) {
            return VIEW_TYPE_EDIT_INFO;
        } else if (titles != null) {
            return VIEW_TYPE_HELPER_TEXT;
        } else {
            return VIEW_TYPE_EMPTY;
        }
    }

    @Override
    public int getItemCount() {
        if (mUser != null && isNormalInfo()) {
            return 7;
        } else {
            return 1;
        }
    }

    public boolean isNormalInfo() {
        return mUser.getNumber() != null ||
                mUser.getBirthday() != null || mUser.getCountry() != null ||
                mUser.getRepublic() != null || mUser.getCity() != null ||
                mUser.getEducation() != null || mUser.getGender() != null;
    }

    public User getInfo() {
        return infoUser;
    }

    public class DetailedViewHolder extends BaseViewHolder {
        @BindView(R.id.title)
        public TextInputEditText title;
        @BindView(R.id.outlinedTextField)
        public TextInputLayout textInputLayout;


        public DetailedViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onBind(int position) {
            super.onBind(position);
            if (mUser.getNumber() != null) {
                title.setText(mUser.getNumber());
                textInputLayout.setStartIconDrawable(R.drawable.ic_phone);
                mUser.setNumber(null);
            } else if (mUser.getBirthday() != null) {
                String userBirthday = mUser.getBirthday();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat serverDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                @SuppressLint("SimpleDateFormat") SimpleDateFormat clientDateFormat = new SimpleDateFormat("dd.MM.yyyy");
                try {
                    Date date = serverDateFormat.parse(userBirthday);
                    assert date != null;
                    title.setText(clientDateFormat.format(date));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                textInputLayout.setStartIconDrawable(R.drawable.ic_cake);
                mUser.setBirthday(null);
            } else if (mUser.getCountry() != null) {
                title.setText(mUser.getCountry());
                textInputLayout.setStartIconDrawable(R.drawable.ic_flag);
                mUser.setCountry(null);
            } else if (mUser.getRepublic() != null) {
                title.setText(mUser.getRepublic());
                textInputLayout.setStartIconDrawable(R.drawable.ic_republic);
                mUser.setRepublic(null);
            } else if (mUser.getCity() != null) {
                title.setText(mUser.getCity());
                textInputLayout.setStartIconDrawable(R.drawable.ic_location_city);
                mUser.setCity(null);
            } else if (mUser.getEducation() != null) {
                title.setText(mUser.getEducation());
                textInputLayout.setStartIconDrawable(R.drawable.ic_school);
                mUser.setEducation(null);
            } else if (mUser.getGender() != null && mUser.getGender() != -1) {
                if (mUser.getGender() == 0) {
                    title.setText(R.string.full_gender_w);
                } else if (mUser.getGender() == 1) {
                    title.setText(R.string.full_gender_m);
                } else {
                    title.setText(R.string.full_gender_not_defined);
                }
                textInputLayout.setStartIconDrawable(R.drawable.ic_gender);
                mUser.setGender(-1);
            } else {
                itemView.setVisibility(View.GONE);
            }
        }

        @Override
        protected void clear() {
            title.setText("");
            textInputLayout.setStartIconDrawable(R.drawable.ic_aim);
        }
    }

    public class EditViewHolder extends BaseViewHolder {
        @BindView(R.id.title)
        public TextInputEditText title;
        @BindView(R.id.filled_exposed_dropdown)
        public AutoCompleteTextView filledExposedDropdown;
        @BindView(R.id.outlinedTextField)
        public TextInputLayout textInputLayout;
        @BindView(R.id.outlinedTextField2)
        public TextInputLayout textInputLayout2;


        public EditViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onBind(int position) {
            super.onBind(position);
            title.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (textInputLayout.getStartIconContentDescription() != null && s != null) {
                        if (textInputLayout.getStartIconContentDescription().equals(Objects.requireNonNull(mRouter.getActivity()).getString(R.string.hint_number))) {
                            infoUser.setNumber(s.toString());
                        } else if (textInputLayout.getStartIconContentDescription().equals(mRouter.getActivity().getString(R.string.birthday))) {
                            infoUser.setBirthday(s.toString());
                        } else if (textInputLayout.getStartIconContentDescription().equals(mRouter.getActivity().getString(R.string.country))) {
                            infoUser.setCountry(s.toString());
                        } else if (textInputLayout.getStartIconContentDescription().equals(mRouter.getActivity().getString(R.string.republic))) {
                            infoUser.setRepublic(s.toString());
                        } else if (textInputLayout.getStartIconContentDescription().equals(mRouter.getActivity().getString(R.string.city))) {
                            infoUser.setCity(s.toString());
                        } else if (textInputLayout.getStartIconContentDescription().equals(mRouter.getActivity().getString(R.string.education))) {
                            infoUser.setEducation(s.toString());
                        } else if (textInputLayout.getStartIconContentDescription().equals(mRouter.getActivity().getString(R.string.gender))) {
                            if (s.toString().equals(mRouter.getActivity().getString(R.string.full_gender_w))) {
                                infoUser.setGender(0);
                            } else if (s.toString().equals(mRouter.getActivity().getString(R.string.full_gender_m))) {
                                infoUser.setGender(1);
                            } else if (s.toString().equals(mRouter.getActivity().getString(R.string.full_gender_not_defined))) {
                                infoUser.setGender(2);
                            } else {
                                infoUser.setGender(-2);
                            }
                        }
                    }
                }
            });
            if (mUser.getNumber() != null && !mUser.getNumber().equals("null")) {
                title.setText(mUser.getNumber());
                textInputLayout.setStartIconDrawable(R.drawable.ic_phone);
                textInputLayout.setStartIconContentDescription(R.string.hint_number);
                mUser.setNumber("null");
            } else if (mUser.getNumber() == null) {
                title.setHint(R.string.hint_number);
                textInputLayout.setStartIconDrawable(R.drawable.ic_phone);
                textInputLayout.setStartIconContentDescription(R.string.hint_number);
                mUser.setNumber("null");
            } else if (mUser.getBirthday() != null && !mUser.getBirthday().equals("null")) {
                String userBirthday = mUser.getBirthday();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat serverDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                @SuppressLint("SimpleDateFormat") SimpleDateFormat clientDateFormat = new SimpleDateFormat("dd.MM.yyyy");
                try {
                    Date date = serverDateFormat.parse(userBirthday);
                    assert date != null;
                    title.setText(clientDateFormat.format(date));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                textInputLayout.setStartIconDrawable(R.drawable.ic_cake);
                textInputLayout.setStartIconContentDescription(R.string.birthday);
                mUser.setBirthday("null");
                title.setInputType(InputType.TYPE_NULL);
                title.setOnFocusChangeListener((v, hasFocus) -> {
                    if (hasFocus) {
                        setCalendar();
                    }
                });
                title.setOnClickListener(v -> setCalendar());
            } else if (mUser.getBirthday() == null) {
                title.setHint(R.string.birthday);
                textInputLayout.setStartIconDrawable(R.drawable.ic_cake);
                textInputLayout.setStartIconContentDescription(R.string.birthday);
                mUser.setBirthday("null");
                title.setInputType(InputType.TYPE_NULL);
                title.setOnFocusChangeListener((v, hasFocus) -> {
                    if (hasFocus) {
                        setCalendar();
                    }
                });
                title.setOnClickListener(v -> setCalendar());
            } else if (mUser.getCountry() != null && !mUser.getCountry().equals("null")) {
                title.setText(mUser.getCountry());
                textInputLayout.setStartIconDrawable(R.drawable.ic_flag);
                textInputLayout.setStartIconContentDescription(R.string.country);
                mUser.setCountry("null");
            } else if (mUser.getCountry() == null) {
                title.setHint(R.string.country);
                textInputLayout.setStartIconDrawable(R.drawable.ic_flag);
                textInputLayout.setStartIconContentDescription(R.string.country);
                mUser.setCountry("null");
            } else if (mUser.getRepublic() != null && !mUser.getRepublic().equals("null")) {
                title.setText(mUser.getRepublic());
                textInputLayout.setStartIconDrawable(R.drawable.ic_republic);
                textInputLayout.setStartIconContentDescription(R.string.republic);
                mUser.setRepublic("null");
            } else if (mUser.getRepublic() == null) {
                title.setHint(R.string.republic);
                textInputLayout.setStartIconDrawable(R.drawable.ic_republic);
                textInputLayout.setStartIconContentDescription(R.string.republic);
                mUser.setRepublic("null");
            } else if (mUser.getCity() != null && !mUser.getCity().equals("null")) {
                title.setText(mUser.getCity());
                textInputLayout.setStartIconDrawable(R.drawable.ic_location_city);
                textInputLayout.setStartIconContentDescription(R.string.city);
                mUser.setCity("null");
            } else if (mUser.getCity() == null) {
                title.setHint(R.string.city);
                textInputLayout.setStartIconDrawable(R.drawable.ic_location_city);
                textInputLayout.setStartIconContentDescription(R.string.city);
                mUser.setCity("null");
            } else if (mUser.getEducation() != null && !mUser.getEducation().equals("null")) {
                title.setText(mUser.getEducation());
                textInputLayout.setStartIconDrawable(R.drawable.ic_school);
                textInputLayout.setStartIconContentDescription(R.string.education);
                mUser.setEducation("null");
            } else if (mUser.getEducation() == null) {
                title.setHint(R.string.education);
                textInputLayout.setStartIconDrawable(R.drawable.ic_school);
                textInputLayout.setStartIconContentDescription(R.string.education);
                mUser.setEducation("null");
            } else if (mUser.getGender() != null && mUser.getGender() != -1) {
                textInputLayout.setVisibility(View.GONE);
                textInputLayout2.setVisibility(View.VISIBLE);
                String[] items = new String[]{Objects.requireNonNull(mRouter.getActivity()).getString(R.string.full_gender_w),
                        Objects.requireNonNull(mRouter.getActivity()).getString(R.string.full_gender_m),
                        Objects.requireNonNull(mRouter.getActivity()).getString(R.string.full_gender_not_defined)};

                ArrayAdapter adapter = new ArrayAdapter<>(Objects.requireNonNull(mRouter.getActivity()), android.R.layout.simple_spinner_dropdown_item, items);
                filledExposedDropdown.setAdapter(adapter);
                if (mUser.getGender() == 0) {
                    filledExposedDropdown.setText(R.string.full_gender_w);
                } else if (mUser.getGender() == 1) {
                    filledExposedDropdown.setText(R.string.full_gender_m);
                } else {
                    filledExposedDropdown.setText(R.string.full_gender_not_defined);
                }
                textInputLayout2.setStartIconDrawable(R.drawable.ic_gender);
                textInputLayout.setStartIconContentDescription(R.string.gender);
                mUser.setGender(-1);
            } else if (mUser.getGender() == null) {
                textInputLayout.setVisibility(View.GONE);
                textInputLayout2.setVisibility(View.VISIBLE);
                filledExposedDropdown.setInputType(InputType.TYPE_NULL);
                String[] items = new String[]{Objects.requireNonNull(mRouter.getActivity()).getString(R.string.full_gender_w),
                        Objects.requireNonNull(mRouter.getActivity()).getString(R.string.full_gender_m),
                        Objects.requireNonNull(mRouter.getActivity()).getString(R.string.full_gender_not_defined)};

                ArrayAdapter adapter = new ArrayAdapter<>(Objects.requireNonNull(mRouter.getActivity()), android.R.layout.simple_spinner_dropdown_item, items);
                filledExposedDropdown.setAdapter(adapter);
                filledExposedDropdown.setHint(R.string.gender);
                textInputLayout2.setStartIconDrawable(R.drawable.ic_gender);
                textInputLayout.setStartIconContentDescription(R.string.gender);
                mUser.setGender(-1);
            }
        }

        @Override
        protected void clear() {
            title.setText("");
            textInputLayout.setStartIconDrawable(R.drawable.ic_aim);
        }

        private void setCalendar() {
            final Calendar calendar = Calendar.getInstance();
            int curDay = calendar.get(Calendar.DAY_OF_MONTH);
            int curMonth = calendar.get(Calendar.MONTH);
            int curYear = calendar.get(Calendar.YEAR);
            DatePickerDialog picker = new DatePickerDialog(Objects.requireNonNull(mRouter.getActivity()),
                    new DatePickerDialog.OnDateSetListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat format2 = new SimpleDateFormat("dd.MM.yyyy");
                            try {
                                Date choiceDate = format.parse(year + "-" + monthOfYear + "-" + dayOfMonth);
                                Date curDate = format.parse(curYear + "-" + curMonth + "-" + curDay);
                                assert choiceDate != null;
                                assert curDate != null;
                                if (choiceDate.getTime() - curDate.getTime() >= 0) {
                                    // TODO
                                } else {
                                    title.setText(format2.format(Objects.requireNonNull(format2.parse(dayOfMonth + "." + (monthOfYear + 1) + "." + year))));
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }, curYear, curMonth, curDay);
            picker.show();
        }
    }

    public class HelperTextViewHolder extends BaseViewHolder {
        @BindView(R.id.title)
        public TextInputEditText title;
        @BindView(R.id.outlinedTextField)
        public TextInputLayout textInputLayout;


        public HelperTextViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onBind(int position) {
            super.onBind(position);
        }

        @Override
        protected void clear() {

        }
    }

    public static class EmptyViewHolder extends BaseViewHolder {
        EmptyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        protected void clear() {
        }
    }
}
