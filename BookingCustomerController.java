package com.momento.controller;

import java.util.List;
import java.util.NoSuchElementException;
//keshav
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.momento.business.BookingCustomerBusiness;
import com.momento.business.HotelBusiness;
import com.momento.framework.Constants;
import com.momento.model.view.BookingRoomDetailsProjection;
import com.momento.model.view.CustomerMasterViewModel;
import com.momento.model.view.CustomerViewModel;
import com.momento.model.view.NextBookedPackageViewModel;

@RestController
@RequestMapping("/momento/bookingCustomer")
public class BookingCustomerController {

	@Autowired
	BookingCustomerBusiness bookingCustomerBusiness;

	@Autowired
	HotelBusiness hotelBusiness;

	/**
	 * This method will retrieve guest by Id
	 * 
	 * @param customerId
	 * @return BookingCustomer
	 */
	@Secured({ Constants.ROLE_SEARCH_AND_BOOKING_R })
	@GetMapping(value = "/getGuestDetailsByBookingPackage/{id}")
	public ResponseEntity<CustomerViewModel> getDetailsById(@PathVariable("id") Long bookingPackageId) {
		if (bookingPackageId != null) {
			CustomerViewModel customerVM = bookingCustomerBusiness.getGuestDetailsByBookingPackage(bookingPackageId);
			if (customerVM != null)
				return ResponseEntity.ok(customerVM);
		}
		return ResponseEntity.notFound().build();
	}

	@Secured({ Constants.ROLE_SEARCH_AND_BOOKING_CUD })
	@PostMapping(value = "/addGuest")
	public ResponseEntity<NextBookedPackageViewModel> addGuestDetails(
			@RequestParam(value = "bookingPackageId", required = true) Long bookingPackageId,
			@RequestParam(value = "bookingId", required = true) Long bookingId,
			@RequestBody CustomerViewModel customerVM) {

		NextBookedPackageViewModel bookingCustomer = bookingCustomerBusiness.addGuestDetails(bookingPackageId,
				bookingId, customerVM);

		if (bookingCustomer != null) {
			return ResponseEntity.ok(bookingCustomer);
		}
		return ResponseEntity.notFound().build();
	}

	/**
	 * This method will update Customer by ID
	 * 
	 * @param guestId
	 * @return
	 */
	@Secured({ Constants.ROLE_SEARCH_AND_BOOKING_CUD })
	@PutMapping(value = "/updateGuest")
	public ResponseEntity<NextBookedPackageViewModel> updateGuest(@RequestBody CustomerViewModel customerVM,
			@RequestParam(value = "bookingPackageId", required = true) Long bookingPackageId,
			@RequestParam(value = "bookingId", required = true) Long bookingId) {
		NextBookedPackageViewModel bookingCustomer = null;
		// List<BookingCustomerViewModel> listOfBookingCustomer =
		// customerVM.getCustomer();
		// for (BookingCustomerViewModel bookingCustomerVM : listOfBookingCustomer) {
		bookingCustomer = bookingCustomerBusiness.updateGuestDetails(customerVM, bookingPackageId, bookingId);
		// }
		if (bookingCustomer != null) {
			return ResponseEntity.ok(bookingCustomer);
		}

		return ResponseEntity.notFound().build();
	}

	@Secured({ Constants.ROLE_SEARCH_AND_BOOKING_R })
	@GetMapping(value = "/findGuest")
	public ResponseEntity<List<CustomerMasterViewModel>> findGuest(
			@RequestParam(value = "firstName", required = true) String firstName,
			@RequestParam(value = "lastName", required = true) String lastName,
			@RequestParam(value = "postCode", required = true) String postCode) {
		List<CustomerMasterViewModel> customerMasterVM = bookingCustomerBusiness.getPrimaryGuestDetailsExists(firstName,
				lastName, postCode);
		if (customerMasterVM != null)
			return ResponseEntity.ok(customerMasterVM);

		return ResponseEntity.noContent().build();
	}

	/**
	 * This method will search room based on hotelId
	 * 
	 * @param hotelId
	 * @return HotelRooms
	 */
	@Secured({ Constants.ROLE_SEARCH_AND_BOOKING_R })
	@GetMapping(value = "/getHotelAndRoomDetailsByRoomId/{id}")
	public ResponseEntity<BookingRoomDetailsProjection> getHotelAndRoomDetailsByRoomId(
			@PathVariable("id") Long roomId) {
		if (roomId != null) {
			BookingRoomDetailsProjection hotelRoomSummary = hotelBusiness.getHotelAndRoomDetailsByRoomId(roomId);
			return ResponseEntity.ok(hotelRoomSummary);
		} else {
			throw new NoSuchElementException("No.Records.Found");
		}
	}
}
