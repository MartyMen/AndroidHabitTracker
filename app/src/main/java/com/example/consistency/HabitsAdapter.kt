import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.consistency.Habit
import com.example.consistency.R

class HabitsAdapter(private val habits: List<Habit>) : RecyclerView.Adapter<HabitsAdapter.HabitViewHolder>() {

    private val selectedHabits = mutableSetOf<String>()

    class HabitViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkBoxHabit: CheckBox = view.findViewById(R.id.checkBoxHabit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_habit, parent, false)
        return HabitViewHolder(view)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val habit = habits[position]
        holder.checkBoxHabit.text = habit.habitName
        holder.checkBoxHabit.isChecked = selectedHabits.contains(habit.habitId)

        holder.checkBoxHabit.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedHabits.add(habit.habitId)
            } else {
                selectedHabits.remove(habit.habitId)
            }
        }
    }

    fun getSelectedHabitNames(selectedIds: Set<String>): List<String> {
        return habits.filter { it.habitId in selectedIds }.map { it.habitName }
    }

    fun getSelectedHabits(): Set<String> {
        return selectedHabits
    }

    override fun getItemCount() = habits.size
}
