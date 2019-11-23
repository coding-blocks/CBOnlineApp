package com.codingblocks.cbonlineapp.mycourse.doubts

//class DoubtsFragment : Fragment(), AnkoLogger {
//
//    private val attemptId: String by lazy {
//        arguments?.getString(ARG_ATTEMPT_ID) ?: ""
//    }
//    private val courseId: String by lazy {
//        arguments?.getString(COURSE_ID) ?: ""
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ):
//        View? = inflater.inflate(R.layout.fragment_doubts, container, false).apply {
//    }
//
//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
////        val doubtsAdapter = DoubtsAdapter(ArrayList())
////        doubtsRv.layoutManager = LinearLayoutManager(context)
////        doubtsRv.adapter = doubtsAdapter
//        val itemDecorator = DividerItemDecoration(context!!, DividerItemDecoration.VERTICAL)
//        itemDecorator.setDrawable(ContextCompat.getDrawable(context!!, R.drawable.divider_black)!!)
//        doubtsRv.addItemDecoration(itemDecorator)
////        Clients.api.getDoubts(courseId).enqueue(retrofitCallback { _, doubtsresponse ->
////            doubtsresponse?.body().let {
////                it?.topicList?.topics?.let { it1 -> doubtsAdapter.setData(it1) }
////            }
////        })
//    }
//
//    companion object {
//
//        @JvmStatic
//        fun newInstance(param1: String, crUid: String) =
//            DoubtsFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_ATTEMPT_ID, param1)
//                    putString(COURSE_ID, crUid)
//                }
//            }
//    }
//}
