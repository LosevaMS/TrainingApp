package Fragments;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavArgument;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.globusproject.InlineExercises;
import com.example.globusproject.R;
import com.example.globusproject.SharedViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

import Adapters.InlineExercisesAdapter;

public class InlineExercisesListFragment extends Fragment implements InlineExercisesAdapter.ClickListener {

    private ArrayList<InlineExercises> inlineExercisesList = new ArrayList<>();
    private SharedViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_exercises_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BottomNavigationView navBar = getActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.GONE);


        if (inlineExercisesList.isEmpty()) {
            createList();
        }

        RecyclerView recyclerView = view.findViewById(R.id.inline_ex_list_recyclerview);
        final InlineExercisesAdapter inlineExercisesAdapter = new InlineExercisesAdapter(inlineExercisesList,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(inlineExercisesAdapter);


        OnBackPressedCallback callback = new OnBackPressedCallback(true ) {
            @Override
            public void handleOnBackPressed() {
                ArrayList<InlineExercises> selectedExercises = inlineExercisesAdapter.getSelected();

                viewModel = ViewModelProviders.of(requireActivity()).get(SharedViewModel.class);

                viewModel.setText(selectedExercises);
                final NavController navController = Navigation.findNavController(getView());
                if (!navController.popBackStack()) {
                    navController.navigate(R.id.action_fragment_inline_exercises_to_edit_training);
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

    }


    private boolean[] toPrimitiveArray(final ArrayList<Boolean> booleanList) {
        final boolean[] primitives = new boolean[booleanList.size()];
        int index = 0;
        for (Boolean object : booleanList) {
            primitives[index++] = object;
        }
        return primitives;
    }

    @Override
    public void onItemClick(int position, View v) {
    }

    public void createList(){
        inlineExercisesList.add(new InlineExercises("Бицепс"));
        inlineExercisesList.add(new InlineExercises("Подъемы гантелей на бицепс","https://i2.wampi.ru/2020/05/05/biceps1.gif"));
        inlineExercisesList.add(new InlineExercises("Подъемы гантелей на бицепс с супинацией","https://ia.wampi.ru/2020/05/05/biceps2.gif"));
        inlineExercisesList.add(new InlineExercises("Подъемы гантелей на бицепс молотком","https://i2.wampi.ru/2020/05/05/biceps3.gif"));
        inlineExercisesList.add(new InlineExercises("Обратные подъемы гантелей на бицепс","https://ia.wampi.ru/2020/05/05/biceps4.gif"));
        inlineExercisesList.add(new InlineExercises("Концентрированный подъем гантелей на бицепс","https://i2.wampi.ru/2020/05/05/biceps5.gif"));
        inlineExercisesList.add(new InlineExercises("Подъемы гантелей на наклонной скамье","https://ia.wampi.ru/2020/05/05/biceps6.gif"));
        inlineExercisesList.add(new InlineExercises("Подъемы гантелей на бицепс с опорой на наклонную скамью","https://i2.wampi.ru/2020/05/05/biceps7.gif"));
        inlineExercisesList.add(new InlineExercises("Паучьи подъемы гантелей на бицепс","https://ia.wampi.ru/2020/05/05/biceps8.gif"));
        inlineExercisesList.add(new InlineExercises("Перекрестные подъемы гантелей на бицепс","https://i2.wampi.ru/2020/05/05/biceps9.gif"));
        inlineExercisesList.add(new InlineExercises("Подъемы штанги на бицепс","https://ia.wampi.ru/2020/05/05/biceps10.gif"));
        inlineExercisesList.add(new InlineExercises("Обратные подъемы штанги на бицепс","https://i2.wampi.ru/2020/05/05/biceps11.gif"));
        inlineExercisesList.add(new InlineExercises("Паучьи подъемы штанги на бицепс","https://ia.wampi.ru/2020/05/05/biceps12.gif"));
        inlineExercisesList.add(new InlineExercises("Сгибание рук на бицепс в кроссовере","https://i2.wampi.ru/2020/05/05/biceps13.gif"));

        inlineExercisesList.add(new InlineExercises("Трицепс"));
        inlineExercisesList.add(new InlineExercises("Разгибание гантелей из-за головы","https://ia.wampi.ru/2020/05/05/triceps1.gif"));
        inlineExercisesList.add(new InlineExercises("Разгибание рук с гантелями в наклоне","https://i2.wampi.ru/2020/05/05/triceps2.gif"));
        inlineExercisesList.add(new InlineExercises("Разгибание рук с гантелями с опорой на скамью","https://ia.wampi.ru/2020/05/05/triceps3.gif"));
        inlineExercisesList.add(new InlineExercises("Разгибание рук с гантелями лежа","https://i2.wampi.ru/2020/05/05/triceps4.gif"));
        inlineExercisesList.add(new InlineExercises("Отжимания с узкой постановкой рук","https://ia.wampi.ru/2020/05/05/triceps5.gif"));
        inlineExercisesList.add(new InlineExercises("Обратные отжимания от скамьи","https://i2.wampi.ru/2020/05/05/triceps6.gif"));
        inlineExercisesList.add(new InlineExercises("Сгибание рук на трицепс в кроссовере","https://i2.wampi.ru/2020/05/05/triceps8.gif"));


        inlineExercisesList.add(new InlineExercises("Плечи"));
        inlineExercisesList.add(new InlineExercises("Жим гантелей над головой","https://ia.wampi.ru/2020/05/05/shoulder1.gif"));
        inlineExercisesList.add(new InlineExercises("Подъем гантелей перед собой","https://i2.wampi.ru/2020/05/05/shoulder2.gif"));
        inlineExercisesList.add(new InlineExercises("Разведение гантелей в стороны","https://ia.wampi.ru/2020/05/05/shoulder3.gif"));
        inlineExercisesList.add(new InlineExercises("Разведение гантелей в стороны в наклоне","https://i2.wampi.ru/2020/05/05/shoulder4.gif"));
        inlineExercisesList.add(new InlineExercises("Тяга гантелей к подбородку","https://ia.wampi.ru/2020/05/05/shoulder5.gif"));
        inlineExercisesList.add(new InlineExercises("Жим Арнольда","https://i2.wampi.ru/2020/05/05/shoulder6.gif"));
        inlineExercisesList.add(new InlineExercises("Параллельный жим гантелей над головой","https://ia.wampi.ru/2020/05/05/shoulder7.gif"));
        inlineExercisesList.add(new InlineExercises("Разведение гантелей в стороны лежа лицом вниз","https://i2.wampi.ru/2020/05/05/shoulder8.gif"));

        inlineExercisesList.add(new InlineExercises("Бедра"));
        inlineExercisesList.add(new InlineExercises("Сумо-приседания с гантелями","https://ia.wampi.ru/2020/05/05/thigh1.gif"));
        inlineExercisesList.add(new InlineExercises("Румынская тяга с гантелями","https://i2.wampi.ru/2020/05/05/thigh2.gif"));
        inlineExercisesList.add(new InlineExercises("Обратные выпады с гантелями","https://ia.wampi.ru/2020/05/05/thigh3.gif"));
        inlineExercisesList.add(new InlineExercises("Румынская тяга с гантелями с отведением прямой ноги","https://i2.wampi.ru/2020/05/05/thigh4.gif"));
        inlineExercisesList.add(new InlineExercises("Мах ногой вверх с гантелью","https://ia.wampi.ru/2020/05/05/thigh5.gif"));
        inlineExercisesList.add(new InlineExercises("Ягодичный мостик с гантелями","https://i2.wampi.ru/2020/05/05/thigh6.gif"));
        inlineExercisesList.add(new InlineExercises("Приседания с гантелями с отведением ноги назад","https://ia.wampi.ru/2020/05/05/thigh7.gif"));
        inlineExercisesList.add(new InlineExercises("Болгарский выпад с гантелями","https://i2.wampi.ru/2020/05/05/thigh8.gif"));
        inlineExercisesList.add(new InlineExercises("Румынская тяга с гантелями на одной ноге","https://ia.wampi.ru/2020/05/05/thigh9.gif"));
        inlineExercisesList.add(new InlineExercises("Приседания на коленях с гантелями","https://i2.wampi.ru/2020/05/05/thigh10.gif"));
        inlineExercisesList.add(new InlineExercises("Мостик с гантелями с поднятой ногой","https://ia.wampi.ru/2020/05/05/thigh11.gif"));
        inlineExercisesList.add(new InlineExercises("Подъем гантелей лежа на животе","https://i2.wampi.ru/2020/05/05/thigh12.gif"));

        inlineExercisesList.add(new InlineExercises("Грудь + Спина"));
        inlineExercisesList.add(new InlineExercises("Жим гантелей на горизонтальной скамье", "https://ia.wampi.ru/2020/05/05/chest1.gif"));
        inlineExercisesList.add(new InlineExercises("Разведение рук с гантелями на горизонтальной скамье","https://i2.wampi.ru/2020/05/05/chest2.gif"));
        inlineExercisesList.add(new InlineExercises("Жим гантелей лежа на скамье хватом «Молоток»","https://ia.wampi.ru/2020/05/05/chest3.gif"));
        inlineExercisesList.add(new InlineExercises("Жим гантелей на наклонной скамье","https://i2.wampi.ru/2020/05/05/chest4.gif"));
        inlineExercisesList.add(new InlineExercises("Разведение рук с гантелями на наклонной скамье","https://ia.wampi.ru/2020/05/05/chest5.gif"));
        inlineExercisesList.add(new InlineExercises("Жим гантелей лежа на наклонной скамье хватом «Молоток»","https://i2.wampi.ru/2020/05/05/chest6.gif"));
        inlineExercisesList.add(new InlineExercises("Жим гантелей на наклонной скамье вниз головой","https://ia.wampi.ru/2020/05/05/chest7.gif"));
        inlineExercisesList.add(new InlineExercises("Разведение гантелей вниз головой","https://i2.wampi.ru/2020/05/05/chest8.gif"));
        inlineExercisesList.add(new InlineExercises("Жим гантелей на полу","https://ia.wampi.ru/2020/05/05/chest9.gif"));
        inlineExercisesList.add(new InlineExercises("Разведение гантелей лежа на полу","https://i2.wampi.ru/2020/05/05/chest10.gif"));
        inlineExercisesList.add(new InlineExercises("Пуловер","https://ia.wampi.ru/2020/05/05/chest11.gif"));
        inlineExercisesList.add(new InlineExercises("Вертикальная тяга в наклоне","https://i2.wampi.ru/2020/05/05/chest12.gif"));
        inlineExercisesList.add(new InlineExercises("Тяга верхнего блока к груди широким хватом","https://ia.wampi.ru/2020/05/05/chest13.gif"));
        inlineExercisesList.add(new InlineExercises("Отжимания на брусьях","https://i2.wampi.ru/2020/05/05/chest14.gif"));
        inlineExercisesList.add(new InlineExercises("Тяга одной рукой к животу","https://i2.wampi.ru/2020/05/05/chest15.gif"));

        inlineExercisesList.add(new InlineExercises("Ноги + ягодицы"));
        inlineExercisesList.add(new InlineExercises("Приседания со штангой","https://i2.wampi.ru/2020/05/05/legs_buttocks1.gif"));
        inlineExercisesList.add(new InlineExercises("Сумо-приседания со штангой","https://ia.wampi.ru/2020/05/05/legs_buttocks2.gif"));
        inlineExercisesList.add(new InlineExercises("Фронтальные приседания со штангой","https://i2.wampi.ru/2020/05/05/legs_buttocks3.gif"));
        inlineExercisesList.add(new InlineExercises("Становая тяга со штангой","https://ia.wampi.ru/2020/05/05/legs_buttocks4.gif"));
        inlineExercisesList.add(new InlineExercises("Румынская тяга со штангой","https://i2.wampi.ru/2020/05/05/legs_buttocks5.gif"));
        inlineExercisesList.add(new InlineExercises("Выпады на месте со штангой","https://ia.wampi.ru/2020/05/05/legs_buttocks6.gif"));
        inlineExercisesList.add(new InlineExercises("Выпады вперед со штангой","https://i2.wampi.ru/2020/05/05/legs_buttocks7.gif"));
        inlineExercisesList.add(new InlineExercises("Выпады назад со штангой","https://ia.wampi.ru/2020/05/05/legs_buttocks8.gif"));
        inlineExercisesList.add(new InlineExercises("Болгарский присед со штангой","https://i2.wampi.ru/2020/05/05/legs_buttocks9.gif"));
        inlineExercisesList.add(new InlineExercises("Боковой выпад со штангой","https://ia.wampi.ru/2020/05/05/legs_buttocks10.gif"));
        inlineExercisesList.add(new InlineExercises("Боковой выпад со штангой на месте","https://i2.wampi.ru/2020/05/05/legs_buttocks11.gif"));
        inlineExercisesList.add(new InlineExercises("Зашагивания на скамью со штангой","https://ia.wampi.ru/2020/05/05/legs_buttocks12.gif"));
        inlineExercisesList.add(new InlineExercises("Подъем на носки со штангой","https://i2.wampi.ru/2020/05/05/legs_buttocks13.gif"));
        inlineExercisesList.add(new InlineExercises("Мостик со штангой","https://ia.wampi.ru/2020/05/05/legs_buttocks14.gif"));
        inlineExercisesList.add(new InlineExercises("Приседания-плие со штангой","https://i2.wampi.ru/2020/05/05/legs_buttocks15.gif"));
        inlineExercisesList.add(new InlineExercises("Приседания с гантелями","https://ia.wampi.ru/2020/05/05/legs_buttocks16.gif"));
        inlineExercisesList.add(new InlineExercises("Кубковые приседания с гантелью","https://i2.wampi.ru/2020/05/05/legs_buttocks17.gif"));
        inlineExercisesList.add(new InlineExercises("Румынская тяга с гантелями","https://ia.wampi.ru/2020/05/05/legs_buttocks18.gif"));
        inlineExercisesList.add(new InlineExercises("Разгибание ног в тренажере","https://i2.wampi.ru/2020/05/05/legs_buttocks19.gif"));
        inlineExercisesList.add(new InlineExercises("Сгибание ног в тренажере","https://ia.wampi.ru/2020/05/05/legs_buttocks20.gif"));

        inlineExercisesList.add(new InlineExercises("Шея"));
        inlineExercisesList.add(new InlineExercises("Сгибание шеи лежа на скамье","https://i2.wampi.ru/2020/05/05/neck1.gif"));
        inlineExercisesList.add(new InlineExercises("Разгибание шеи лежа на скамье","https://ia.wampi.ru/2020/05/05/neck2.gif"));
        inlineExercisesList.add(new InlineExercises("Сгибание шеи с головным шлемом","https://i2.wampi.ru/2020/05/05/neck3.gif"));

        inlineExercisesList.add(new InlineExercises("Пресс"));
        inlineExercisesList.add(new InlineExercises("Скручивания","https://i2.wampi.ru/2020/05/05/press1.gif"));
        inlineExercisesList.add(new InlineExercises("Сотня","https://ia.wampi.ru/2020/05/05/press2.gif"));
        inlineExercisesList.add(new InlineExercises("Охотничья собака","https://i2.wampi.ru/2020/05/05/press3.gif"));
        inlineExercisesList.add(new InlineExercises("Разгибания ног под углом лежа","https://ia.wampi.ru/2020/05/05/press4.gif"));
        inlineExercisesList.add(new InlineExercises("Повороты корпуса полусидя","https://i2.wampi.ru/2020/05/05/press5.gif"));
        inlineExercisesList.add(new InlineExercises("Динамичная планка на коленях","https://ia.wampi.ru/2020/05/05/press6.gif"));
        inlineExercisesList.add(new InlineExercises("Касание носками пола из положения лежа","https://i2.wampi.ru/2020/05/05/press7.gif"));
        inlineExercisesList.add(new InlineExercises("Подтягивание коленей полусидя","https://ia.wampi.ru/2020/05/05/press8.gif"));
        inlineExercisesList.add(new InlineExercises("Боковые наклоны лежа","https://i2.wampi.ru/2020/05/05/press9.gif"));
        inlineExercisesList.add(new InlineExercises("Подъемы корпуса лежа на животе","https://ia.wampi.ru/2020/05/05/press10.gif"));
        inlineExercisesList.add(new InlineExercises("Планка на предплечьях на одной ноге","https://i2.wampi.ru/2020/05/05/press11.gif"));
        inlineExercisesList.add(new InlineExercises("Поза лодочки","https://ia.wampi.ru/2020/05/05/press12.gif"));
        inlineExercisesList.add(new InlineExercises("Боковая планка с упором на локоть","https://i2.wampi.ru/2020/05/05/press13.gif"));
        inlineExercisesList.add(new InlineExercises("Уголок","https://ia.wampi.ru/2020/05/05/press14.gif"));
        inlineExercisesList.add(new InlineExercises("Боковая планка на прямой руке","https://i2.wampi.ru/2020/05/05/press15.gif"));
        inlineExercisesList.add(new InlineExercises("Обратная планка","https://ia.wampi.ru/2020/05/05/press16.gif"));
        inlineExercisesList.add(new InlineExercises("Супермен","https://i2.wampi.ru/2020/05/05/press17.gif"));
        inlineExercisesList.add(new InlineExercises("Планка на прямых руках","https://ia.wampi.ru/2020/05/05/press18.gif"));
        inlineExercisesList.add(new InlineExercises("Планка на предплечьях","https://ia.wampi.ru/2020/05/05/press19.gif"));

    }

}