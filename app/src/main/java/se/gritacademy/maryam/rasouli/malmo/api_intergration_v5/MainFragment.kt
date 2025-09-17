package se.gritacademy.maryam.rasouli.malmo.api_intergration_v5

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import se.gritacademy.maryam.rasouli.malmo.api_intergration_v5.databinding.FragmentMainBinding

/**
 * Startfragment
 * Navigation: från Main -> Fragment1 och vidare
 */
class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    /**
     * Här skapas fragmentets vy och initierar navigation till Fragment1
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        // Här navigeras till Fragment1 när knappen trycks
        binding.buttonToFragment1.setOnClickListener {
            findNavController().navigate(R.id.action_main_to_fragment1)
        }

        return binding.root
    }

    /**
     * Rensar view binding för att undvika memory leaks
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
