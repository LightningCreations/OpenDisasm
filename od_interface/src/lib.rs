use core::alloc::Layout;

#[no_mangle]
pub extern "C" fn xlang_allocate(size: usize) -> *mut core::ffi::c_void {
    if size == 0 {
        return 32usize as *mut core::ffi::c_void;
    }
    unsafe {
        xlang_allocate_aligned(
            size,
            if size > 32 {
                32
            } else {
                size.next_power_of_two()
            },
        )
    }
}

#[allow(clippy::missing_safety_doc)]
#[no_mangle]
pub unsafe extern "C" fn xlang_allocate_aligned(
    size: usize,
    align: usize,
) -> *mut core::ffi::c_void {
    if size == 0 {
        return align as *mut core::ffi::c_void;
    }
    let size = size + (align - size % align) % align;
    let layout = Layout::from_size_align_unchecked(size, align);
    std::alloc::alloc(layout).cast::<_>()
}

#[allow(clippy::missing_safety_doc)]
#[no_mangle]
pub unsafe extern "C" fn xlang_deallocate_aligned(
    ptr: *mut core::ffi::c_void,
    size: usize,
    align: usize,
) {
    if size == 0 || ptr.is_null() {
        return;
    }
    let size = size + (align - size % align) % align;
    let layout = Layout::from_size_align_unchecked(size, align);
    std::alloc::dealloc(ptr.cast::<_>(), layout);
}

#[no_mangle]
pub extern "C" fn xlang_on_allocation_failure(size: usize, align: usize) -> ! {
    eprintln!(
        "Failed to allocate with size: {}, and alignment: {}",
        size, align
    );
    std::process::abort()
}
