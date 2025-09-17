package se.gritacademy.maryam.rasouli.malmo.api_intergration_v5

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import se.gritacademy.maryam.rasouli.malmo.api_intergration_v5.databinding.Fragment1Binding

/**
 * Navigation: (MainFragment) -> Fragment1 -> Fragment2 / Fragment3  sen Tillbaka till Fragment1 sen till (Mainfragment)
 * Alla knappar finns i både portrait och landscape
 */
class Fragment1 : Fragment() {

    private var _binding: Fragment1Binding? = null
    private val binding get() = _binding!!

    /**
     * Kallas när fragmentets vy ska skapas.
     * @param inflater LayoutInflater som används för att skapa vyer
     * @param container Eventuell förälder som fragmentets vy ska fästas vid.
     * @param savedInstanceState Bundle med tidigare sparat tillstånd om det finns
     * @return Root View för fragmentets layout
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = Fragment1Binding.inflate(inflater, container, false)

        //Navigerar till Fragment2 vid knapptryckning
        binding.buttonToFragment2.setOnClickListener {
            findNavController().navigate(R.id.action_fragment1_to_fragment2)
        }

        //Navigerar till Fragment3 vid knapptryckning
        binding.buttonToFragment3.setOnClickListener {
            findNavController().navigate(R.id.action_fragment1_to_fragment3)
        }

        // Tillbaka till Main (rensar backstack) vid kanpptryckning.
        binding.buttonBackToMain.setOnClickListener {
            findNavController().popBackStack(R.id.mainFragment, false)
        }

        return binding.root
    }

    /**
     * Den kallas när fragmentets vy förstörs.
     * Den rensar binding för att undvika minnesläckor.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
