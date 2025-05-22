package tg.cos.tomatomall.service.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tg.cos.tomatomall.dto.ChapterCheckoutDTO;
import tg.cos.tomatomall.dto.ChapterEditDTO;
import tg.cos.tomatomall.po.*;
import tg.cos.tomatomall.repository.AccountRepository;
import tg.cos.tomatomall.repository.ChapterRepository;
import tg.cos.tomatomall.repository.OrderRepository;
import tg.cos.tomatomall.repository.ProductRepository;
import tg.cos.tomatomall.service.ChapterService;
import tg.cos.tomatomall.util.OSSUtil;
import tg.cos.tomatomall.util.SecurityUtil;
import tg.cos.tomatomall.vo.CartCheckOutOutputVO;
import tg.cos.tomatomall.vo.ChapterGetAllVO;
import tg.cos.tomatomall.vo.ChapterGetVO;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import org.springframework.mock.web.MockMultipartFile;

@Service
public class ChapterServiceImpl implements ChapterService {

    @Autowired
    private ChapterRepository chapterRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OSSUtil ossUtil;
    @Autowired
    SecurityUtil securityUtil;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private AccountRepository accountRepository;

    @Override
    public String addChapter(ChapterEditDTO chapter) throws IOException {
        Account account = securityUtil.getCurrentUser();
        if (!account.getRole().equalsIgnoreCase("admin")) {
            return "权限不足";
        }
        Optional<Product> productOptional = productRepository.findById(chapter.getProductId());
        if (productOptional.isEmpty()) {
            return "录入章节失败:查找不到该商品";
        }
        Product product = productOptional.get();
        boolean setLast = false;

        Chapter chapterEntity = new Chapter();
        chapterEntity.setName(chapter.getName());
        chapterEntity.setContent(uploadFile(convertStringToMultipartFile(chapter.getContent())));
        BigDecimal price = new BigDecimal(chapter.getContent().length());
        price = price.divide(new BigDecimal(100));
        chapterEntity.setPrice(price);
        chapterEntity.setProduct(product);
        if (chapter.getStatus().equalsIgnoreCase("FREE") ||
                chapter.getStatus().equalsIgnoreCase("CHARGED") ||
                chapter.getStatus().equalsIgnoreCase("LOCKED")) {
            return "录入章节失败:输入的状态有误";
        }
        chapterEntity.setStatus(chapter.getStatus());

        if (chapter.getPrevious() != null) {
            chapterEntity.setPrevious(chapter.getPrevious());
        }else if (!product.getChapters().isEmpty()){
            Chapter last = product.getChapters().getLast();
            int lastId = last.getId();
            chapterEntity.setPrevious(lastId);
//            System.out.println(chapterEntity.getId());
            setLast = true;
        }
        if (chapter.getNext() != null) {
            chapterEntity.setNext(chapter.getNext());
        }
        product.getChapters().add(chapterEntity);
        chapterRepository.save(chapterEntity);
        productRepository.save(product);
        if (setLast) {
            product.getChapters().get(product.getChapters().size() - 2).setNext(chapterEntity.getId());
            productRepository.save(product);
        }

        return "录入章节成功";
    }

    @Override
    public ChapterGetVO getChapter(Integer id) {
        Account account = securityUtil.getCurrentUser();
        List<Chapter> chapters = account.getChapters();
        Optional<Chapter> chapterOptional = chapterRepository.findById(id);
        if (chapterOptional.isEmpty()) {
            return null;
        }
        Chapter chapter = chapterOptional.get();
        ChapterGetVO chapterGetVO = new ChapterGetVO();
        chapterGetVO.setId(chapter.getId());
        chapterGetVO.setName(chapter.getName());
        if (chapter.getPrevious() != null) {
            chapterGetVO.setPrevious(chapter.getPrevious());
        }
        if (chapter.getNext() != null) {
            chapterGetVO.setNext(chapter.getNext());
        }
        chapterGetVO.setProductId(chapter.getProduct().getId());
        chapterGetVO.setStatus(chapter.getStatus());
        if (chapter.getStatus().equalsIgnoreCase("FREE") || chapters.contains(chapter) || account.getRole().equalsIgnoreCase("admin")) {
            chapterGetVO.setContent(getFile(chapter.getContent()));
        }else if (chapter.getStatus().equals("CHARGED")) {
            chapterGetVO.setContent("该章节未购买");
        }else if (chapter.getStatus().equals("LOCKED")) {
            chapterGetVO.setContent("该章节已锁定");
        }

        return chapterGetVO;
    }

    @Override
    public String updateChapter(ChapterEditDTO chapter) throws IOException {
        Account account = securityUtil.getCurrentUser();
        if (!account.getRole().equalsIgnoreCase("admin")) {
            return "权限不足";
        }
        Optional<Chapter> chapterOptional = chapterRepository.findById(chapter.getChapterId());
        if (chapterOptional.isEmpty()) {
            return "查找不到该章节";
        }
        Chapter chapterEntity = chapterOptional.get();

        if (chapter.getName() != null) {
            chapterEntity.setName(chapter.getName());
        }
        if (chapter.getContent() != null) {
            chapterEntity.setContent(uploadFile(convertStringToMultipartFile(chapter.getContent())));
        }
        if (chapter.getPrevious() != null) {
            chapterEntity.setPrevious(chapter.getPrevious());
        }
        if (chapter.getNext() != null) {
            chapterEntity.setNext(chapter.getNext());
        }
        if (chapter.getStatus() != null) {
            if (chapter.getStatus().equalsIgnoreCase("FREE") ||
                    chapter.getStatus().equalsIgnoreCase("CHARGED") ||
                    chapter.getStatus().equalsIgnoreCase("LOCKED")) {
                return "更新章节失败:输入的状态有误";
            }
            chapterEntity.setStatus(chapter.getStatus());
        }
        chapterRepository.save(chapterEntity);

        return "更新章节成功";
    }

    @Override
    public String deleteChapter(Integer id) {
        Account account = securityUtil.getCurrentUser();
        if (!account.getRole().equalsIgnoreCase("admin")) {
            return "权限不足";
        }
        Optional<Chapter> chapterOptional = chapterRepository.findById(id);
        if (chapterOptional.isEmpty()) {
            return "查找不到该章节";
        }
        Chapter chapter = chapterOptional.get();
        chapterRepository.delete(chapter);
        return "删除章节成功";
    }

    @Override
    public List<ChapterGetAllVO> getAllChapter(Integer id) {
        Optional<Product> productOptional = productRepository.findById(id);
        Account account = securityUtil.getCurrentUser();
        if (productOptional.isEmpty()) {
            return null;
        }
        Product product = productOptional.get();
        List<ChapterGetAllVO> chapterGetAllVOList = new ArrayList<>();
        for (Chapter chapter : product.getChapters()) {
            ChapterGetAllVO chapterGetAllVO = new ChapterGetAllVO();
            chapterGetAllVO.setId(chapter.getId());
            chapterGetAllVO.setName(chapter.getName());
            if (chapter.getPrevious() != null) {
                chapterGetAllVO.setPrevious(chapter.getPrevious());
            }
            if (chapter.getNext() != null) {
                chapterGetAllVO.setNext(chapter.getNext());
            }
            chapterGetAllVO.setStatus(chapter.getStatus());
            chapterGetAllVO.setProductId(product.getId());
            if (account.getChapters().contains(chapter)) {
                chapterGetAllVO.setPurchased(true);
            }
            chapterGetAllVOList.add(chapterGetAllVO);
        }
        return chapterGetAllVOList;
    }

    private MultipartFile convertStringToMultipartFile(String text) {
        byte[] content = text.getBytes(StandardCharsets.UTF_8);
        String fileName = "chapter"+UUID.randomUUID().toString().replace("-", "");

        // 创建MockMultipartFile对象
        return new MockMultipartFile(
                fileName,          // 文件名
                fileName,          // 原始文件名
                "text/plain",      // 内容类型
                content            // 内容字节数组
        );
    }

    private String convertMultipartFileToString(MultipartFile file) throws IOException {
        return new String(file.getBytes(), StandardCharsets.UTF_8);
    }

    private String uploadFile(MultipartFile file) throws IOException {
        return ossUtil.upload(file.getName(),file.getInputStream()).replace("http:", "https:");
    }

    public static String getFile(String fileUrl) {
        try {
            URL url = new URL(fileUrl);
            StringBuilder content = new StringBuilder();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            }

            return content.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public CartCheckOutOutputVO checkout(Integer productId, ChapterCheckoutDTO chapterCheckoutDTO){
        Account account = securityUtil.getCurrentUser();

        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isEmpty()) {
            return null;
        }
        Product product = productOptional.get();

        Order order = new Order();
        order.setAccount(account);
        order.setPaymentMethod(chapterCheckoutDTO.getPayment_method());
        order.setName(chapterCheckoutDTO.getShipping_address().getName());
        order.setAddress(chapterCheckoutDTO.getShipping_address().getAddress());
        order.setPhone(chapterCheckoutDTO.getShipping_address().getPhone());

        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setOrder(order);
        orderItem.setQuantity(1);
        orderItem.setAccount(account);
        List<Chapter> chapters = new ArrayList<>();

        for (Integer chapterId : chapterCheckoutDTO.getChapters()){
            Optional<Chapter> chapterOptional = chapterRepository.findById(chapterId);
            if (chapterOptional.isEmpty()) {
                return null;
            }
            Chapter chapter = chapterOptional.get();
            if (!chapter.getProduct().getId().equals(product.getId())) {
                return null;
            }
            chapters.add(chapter);
        }
        orderItem.setChapters(chapters);

        order.setOrderItems(new HashSet<>(List.of(orderItem)));
        order.setTotalAmount(new BigDecimal(1));
        order.setStatus("PENDING");
        order.setCreateTime(new Date());
//        BigDecimal totalAmount = new BigDecimal(String.valueOf(product.getPrice()));
//        double rate = (double) chapterCheckoutDTO.getChapters().size() / product.getChapters().size();
//        totalAmount = totalAmount.multiply(new BigDecimal(rate));
//        order.setTotalAmount(totalAmount);
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Chapter chapter : chapters) {
            totalAmount = totalAmount.add(chapter.getPrice());
        }

        orderRepository.save(order);
        account.getOrders().add(order);
        account.getOrderItems().add(orderItem);
        accountRepository.save(account);

        CartCheckOutOutputVO result = new CartCheckOutOutputVO();
        result.setOrderId(order.getId());
        result.setUsername(account.getUsername());
        result.setTotalAmount(totalAmount);
        result.setPaymentMethod(chapterCheckoutDTO.getPayment_method());
        result.setCreateTime(order.getCreateTime());
        result.setStatus(order.getStatus());
        return result;
    }

    @Override
    public int[] findChaptersBought(Integer productId, Integer accountId) {
        Optional<Account> accountOptional = accountRepository.findById(accountId);
        if (accountOptional.isEmpty()) {
            return null;
        }
        Account account = accountOptional.get();
        Account account1 = securityUtil.getCurrentUser();
        if (!account1.getRole().equalsIgnoreCase("admin") && account.getId() != account1.getId()) {
            return null;
        }
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isEmpty()) {
            return null;
        }
        Product product = productOptional.get();
        List<Chapter> chapters = product.getChapters();
        List<Chapter> chapters1 = account.getChapters();
        List<Chapter> chapters2 = chapters.stream().filter(chapters1::contains).toList();
        int[] result = new int[chapters.size()];
        int index = 0;
        for (Chapter chapter : chapters2) {
            result[index++] = chapter.getId();
            index++;
        }

//        for (Chapter chapter : chapters1) {
//            if (chapter.getProduct().getId().equals(product.getId())) {
//                result[index] = chapter.getId();
//                index++;
//            }
//        }
        int[] res = new int[index];
        System.arraycopy(result, 0, res, 0, index);
        return res;
    }

    @Override
    public int[] findChaptersBought(Integer productId) {
        Account account = securityUtil.getCurrentUser();
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isEmpty()) {
            return null;
        }
        Product product = productOptional.get();
        List<Chapter> chapters = product.getChapters();
        List<Chapter> chapters1 = account.getChapters();
        List<Chapter> chapters2 = chapters.stream().filter(chapters1::contains).toList();
        int[] result = new int[chapters.size()];
        int index = 0;
        for (Chapter chapter : chapters2) {
            result[index++] = chapter.getId();
            index++;
        }
        int[] res = new int[index];
        System.arraycopy(result, 0, res, 0, index);
        return res;
    }
}
